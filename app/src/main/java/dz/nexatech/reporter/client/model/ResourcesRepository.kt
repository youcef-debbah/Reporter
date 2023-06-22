package dz.nexatech.reporter.client.model

import android.content.Context
import androidx.compose.runtime.Stable
import com.google.common.collect.ImmutableMap
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import dz.nexatech.reporter.client.common.AsyncConfig
import dz.nexatech.reporter.client.common.FILE_PATH_SEPARATOR
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.client.core.AbstractBinaryResource
import dz.nexatech.reporter.client.core.CachedResource
import dz.nexatech.reporter.util.model.SimpleCache
import dz.nexatech.reporter.util.model.Teller
import dz.nexatech.reporter.util.ui.ICON_RESOURCE_PREFIX
import dz.nexatech.reporter.util.ui.iconsAssetsResources
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.net.URL
import javax.inject.Inject

const val WEB_RESOURCE_PREFIX = "web/"
const val TEMPLATE_RESOURCE_PREFIX = "templates/"

private val extraAssetsResources: Map<String, AbstractBinaryResource> =
    ImmutableMap.builder<String, AbstractBinaryResource>()
        // extra loadable assets should be added here
        .build()

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
        if (path == "favicon.ico" || path == "") {
            return null
        }

        if (path.startsWith(ICON_RESOURCE_PREFIX)) {
            val iconResource = iconsAssetsResources[path]
            if (iconResource != null) {
                Teller.debug("resource loaded from asset: $path")
                return CachedResource(iconResource)
            }
        }

        val databaseResource = resourcesDAO.get().load(path)
        if (databaseResource != null) {
            Teller.debug("resource loaded from database: $path")
            return databaseResource
        }

        val assetResource =
            if (path.startsWith(FONT_RESOURCE_PREFIX)) fontAssetsResources[path] else extraAssetsResources[path]
        if (assetResource != null) {
            Teller.debug("resource loaded from asset: $path")
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