package com.reporter.client.model

import android.content.Context
import android.net.Uri
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSortedMap
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever
import com.reporter.common.AsyncConfig
import com.reporter.common.MIME_TYPE_FONT_TTF
import com.reporter.common.Webkit
import com.reporter.common.withIO
import com.reporter.util.model.SimpleCache
import com.reporter.util.model.Teller
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.NavigableMap
import javax.inject.Inject

const val WEB_RESOURCE_PREFIX = "web/"
const val FONT_RESOURCE_PREFIX = "fonts/"
const val TEMPLATE_RESOURCE_PREFIX = "templates/"

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
val FONT_ASSETS: Map<String, BinaryResource> = ImmutableMap.builder<String, BinaryResource>()
    .addFont(400, 700, fontDir = "Cairo/")
    .addFont(400, 700, fontDir = "MarkaziText/")
    .addFont(400, 700, fontDir = "ReadexPro/")
    .addFont(400, 700, fontDir = "Vazirmatn/")
    .addFont(400, 700, fontDir = "NotoNaskhArabic/")
    .addFont(400, 700, fontDir = "Lateef/")
    .addFont(400, 700, fontDir = "ScheherazadeNew/")
    .addFont(400, 700, fontDir = "Amiri/")
    .build()

private fun ImmutableMap.Builder<String, BinaryResource>.addFont(
    vararg weights: Int,
    fontDir: String,
): ImmutableMap.Builder<String, BinaryResource> {
    weights.forEach { weight ->
        val path = fontPath(fontDir, weight)
        put(path, AssetResource(path, MIME_TYPE_FONT_TTF))
    }
    return this
}

fun fontPath(fontDir: String, weight: Int) =
    FONT_RESOURCE_PREFIX + fontDir + FONT_WEIGHTS[weight] + ".ttf"

fun fontPaths(fontNames: Collection<String>): List<String> {
    val result = ArrayList<String>(fontNames.size * 2)
    fontNames.forEach {
        val fontDir = it.replace(" ", "") + "/"
        result.add(fontPath(fontDir, 200))
        result.add(fontPath(fontDir, 400))
    }
    return result
}

class ResourcesRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val resourcesDAO: Lazy<ResourcesDAO>,
) : IResourceRetriever {

    private val cache = SimpleCache<BinaryResource>()

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

    suspend fun load(path: String?): BinaryResource? = withIO {
        path?.let { loadCachedBinaryResource(it.removePrefix("/")) }
    }

    fun clearCache() {
        cache.clear()
    }

    suspend fun loadWithoutCache(path: String): BinaryResource? = withIO {
        loadBinaryResource(path)
    }

    private suspend fun loadCachedBinaryResource(path: String): BinaryResource? =
        cache.load(path) { loadBinaryResource(path) }

    private suspend fun loadBinaryResource(path: String): BinaryResource? {
        val resource = resourcesDAO.get().load(path)
        if (resource != null) {
            Teller.debug("resource loaded: $path")
            return resource
        }

        if (path.startsWith(WEB_RESOURCE_PREFIX) || path.startsWith(TEMPLATE_RESOURCE_PREFIX)) {
            return CachedResource(AssetResource(path, Webkit.mimeType(path)))
        } else if (path.startsWith(FONT_RESOURCE_PREFIX)) {
            val fontResource = FONT_ASSETS[path]
            if (fontResource != null) {
                return CachedResource(fontResource)
            }
        }

        Teller.warn("resource not found: $path")
        return null
    }

    fun loadBlocking(path: String?): BinaryResource? = runBlocking(AsyncConfig.ioDispatcher) {
        load(path)
    }

    override fun getInputStreamByUrl(url: URL): InputStream? =
        loadBlocking(url.path)?.asInputStream()

    override fun getByteArrayByUrl(url: URL): ByteArray? =
        loadBlocking(url.path)?.asByteArray()

    suspend fun openSystemContent(uri: Uri): OutputStream? = withIO {
        context.contentResolver.openOutputStream(uri)
    }
}