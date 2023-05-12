package dz.nexatech.reporter.client.model

import android.content.Context
import androidx.compose.runtime.Stable
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSortedMap
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import dz.nexatech.reporter.client.common.AsyncConfig
import dz.nexatech.reporter.client.common.FILE_PATH_SEPARATOR
import dz.nexatech.reporter.client.common.MimeType
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.client.core.AbstractBinaryResource
import dz.nexatech.reporter.client.core.CachedResource
import dz.nexatech.reporter.util.model.SimpleCache
import dz.nexatech.reporter.util.model.Teller
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.net.URL
import java.util.NavigableMap
import javax.inject.Inject

const val WEB_RESOURCE_PREFIX = "web/"
const val FONT_RESOURCE_PREFIX = "fonts/"
const val TEMPLATE_RESOURCE_PREFIX = "templates/"

private val ASSETS: Map<String, AbstractBinaryResource> =
    ImmutableMap.builder<String, AbstractBinaryResource>()
        // loadable assets should be added here
        .build()

private val FONT_WEIGHTS: NavigableMap<Int, String> =
    ImmutableSortedMap.Builder<Int, String>(Integer::compare)
        .put(100, "Thin")
        .put(200, "ExtraLight")
        .put(300, "Light")
        .put(400, "Regular")
        .put(500, "Medium")
        .put(600, "SemiBold")
        .put(700, "Bold")
        .put(800, "ExtraBold")
        .put(900, "Black")
        .build()

// show the fonts in this order in the drop menu
val FONT_ASSETS: Map<String, AbstractBinaryResource> =
    ImmutableMap.builder<String, AbstractBinaryResource>()
        .addFont(400, 700, fontDir = "Cairo/")
        .addFont(400, 700, fontDir = "MarkaziText/")
        .addFont(400, 700, fontDir = "ReadexPro/")
        .addFont(400, 700, fontDir = "Vazirmatn/")
        .addFont(400, 700, fontDir = "NotoNaskhArabic/")
        .addFont(400, 700, fontDir = "Lateef/")
        .addFont(400, 700, fontDir = "ScheherazadeNew/")
        .addFont(400, 700, fontDir = "Amiri/")
        .build()

private fun ImmutableMap.Builder<String, AbstractBinaryResource>.addFont(
    vararg weights: Int,
    fontDir: String,
): ImmutableMap.Builder<String, AbstractBinaryResource> {
    weights.forEach { weight ->
        val path = fontPath(fontDir, weight)
        put(path, AssetResource(path, MimeType.FONT_TTF))
    }
    return this
}

fun fontPath(fontDir: String, weight: Int) =
    FONT_RESOURCE_PREFIX + fontDir + FONT_WEIGHTS[weight] + ".ttf"

fun fontPaths(fontNames: Collection<String>): List<String> {
    val result = ArrayList<String>(fontNames.size * 2)
    fontNames.forEach {
        val fontDir = it.replace(" ", "") + FILE_PATH_SEPARATOR
        result.add(fontPath(fontDir, 200))
        result.add(fontPath(fontDir, 400))
    }
    return result
}

@Stable
class ResourcesRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val resourcesDAO: Lazy<ResourcesDAO>,
) : IResourceRetriever {

    private val cache = SimpleCache<AbstractBinaryResource>()

    suspend fun loadFonts(
        fontNames: Collection<String>
    ): List<ByteArray> = withIO {
        if (fontNames.isEmpty()) {
            return@withIO emptyList()
        } else if (fontNames.size == 1) {
            return@withIO loadAll(fontPaths(fontNames))
        } else {
            return@withIO loadAll(fontPaths(LinkedHashSet(fontNames)))
        }
    }

    suspend fun loadAll(paths: List<String>): List<ByteArray> = withIO {
        paths.map { path ->
            async { load(path)?.asByteArray() }
        }.awaitAll().filterNotNull()
    }

    suspend fun load(path: String?): AbstractBinaryResource? = withIO {
        path?.let { loadCachedBinaryResource(it.removePrefix(FILE_PATH_SEPARATOR)) }
    }

    fun clearCache() {
        cache.clear()
    }

    suspend fun loadWithoutCache(path: String): AbstractBinaryResource? = withIO {
        loadBinaryResource(path)
    }

    private suspend fun loadCachedBinaryResource(path: String): AbstractBinaryResource? =
        cache.load(path) { loadBinaryResource(path) }

    private suspend fun loadBinaryResource(path: String): AbstractBinaryResource? {
        val databaseResource = resourcesDAO.get().load(path)
        if (databaseResource != null) {
            Teller.debug("resource loaded from database: $path")
            return databaseResource
        }

        val assetResource =
            if (path.startsWith(FONT_RESOURCE_PREFIX)) FONT_ASSETS[path] else ASSETS[path]
        if (assetResource != null) {
            Teller.debug("resource loaded from assets: $path")
            return CachedResource(assetResource)
        }

        Teller.warn("resource not found: $path")
        return null
    }

    fun loadBlocking(path: String?): AbstractBinaryResource? =
        runBlocking(AsyncConfig.ioDispatcher) {
            load(path)
        }

    override fun getInputStreamByUrl(url: URL): InputStream? =
        loadBlocking(url.path)?.asInputStream()

    override fun getByteArrayByUrl(url: URL): ByteArray? =
        loadBlocking(url.path)?.asByteArray()

    suspend fun updateResources(resources: List<Resource>?) {
        resources?.let { resourcesDAO.get().updateAll(it) }
    }
}