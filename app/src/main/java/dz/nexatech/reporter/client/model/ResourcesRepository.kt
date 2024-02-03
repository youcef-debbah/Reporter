package dz.nexatech.reporter.client.model

import android.content.Context
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import dz.nexatech.reporter.client.common.AsyncConfig
import dz.nexatech.reporter.client.common.FILE_PATH_SEPARATOR
import dz.nexatech.reporter.client.common.FilesExtension
import dz.nexatech.reporter.client.common.MimeType
import dz.nexatech.reporter.client.common.putValue
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.client.core.AbstractBinaryResource
import dz.nexatech.reporter.client.core.CachedResource
import dz.nexatech.reporter.client.core.IResourceLoader
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

const val RESOURCE_PREFIX = "common/"
private const val CSS_RESOURCES_PREFIX = RESOURCE_PREFIX + "web/"

private val extraAssetsResources: Map<String, AbstractBinaryResource> =
    ImmutableMap.builder<String, AbstractBinaryResource>()
        .putValue(AssetResource(CSS_RESOURCES_PREFIX + "base." + FilesExtension.CSS,MimeType.TEXT_CSS))
        .putValue(AssetResource(CSS_RESOURCES_PREFIX + "normalize." + FilesExtension.CSS,MimeType.TEXT_CSS))
        .putValue(AssetResource(CSS_RESOURCES_PREFIX + "spectre." + FilesExtension.CSS,MimeType.TEXT_CSS))
        .build()

class ResourcesRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val resourcesDAO: Lazy<ResourcesDAO>,
) : IResourceLoader {

    companion object {
        private val cache = SimpleCache<AbstractBinaryResource>()
    }

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

    suspend fun clearCache() {
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

        val databaseResource = resourcesDAO.get().load(path)
        if (databaseResource != null) {
            Teller.debug("database resource loaded: $path")
            return databaseResource
        }

        if (path.startsWith(ICON_RESOURCE_PREFIX)) {
            val iconRes = iconsAssetsResources[path]
            if (iconRes != null) {
                Teller.debug("icon asset loaded: $path")
                return CachedResource(iconRes)
            }
        } else if (path.startsWith(FONT_RESOURCE_PREFIX)) {
            val fontRes = fontAssetsResources[path]
            if (fontRes != null) {
                Teller.debug("font asset loaded: $path")
                return CachedResource(fontRes)
            }
        } else {
            val extraRes = extraAssetsResources[path]
            if (extraRes != null) {
                Teller.debug("extra asset loaded: $path")
                return CachedResource(extraRes)
            }
        }

        Teller.warn("resource not found: $path")
        return null
    }

    fun loadBlocking(path: String?): AbstractBinaryResource? = path?.let {
        runBlocking(AsyncConfig.ioDispatcher) {
            load(path)
        }
    }

    override fun getInputStreamByUrl(url: URL?): InputStream? =
        loadBlocking(url?.path)?.asInputStream()

    override fun getByteArrayByUrl(url: URL?): ByteArray? =
        loadBlocking(url?.path)?.asByteArray()

    suspend fun updateResources(resources: List<Resource>?) {
        resources?.let { resourcesDAO.get().replaceAll(it) }
    }

    suspend fun loadFontFamilies(): ImmutableSet<String> = ImmutableSet.Builder<String>().apply {
        addFamilies(resourcesDAO.get().loadAvailableDynamicFonts())
        addFamilies(fontAssetsResources.keys)
    }.build()

    private fun ImmutableSet.Builder<String>.addFamilies(fontPaths: Iterable<String>) {
        for (fontPath in fontPaths) {
            val familyName = fontFamilyName(fontPath)
            if (familyName.isNotEmpty()) {
                add(familyName)
            }
        }
    }

    fun deleteAll() {
        resourcesDAO.get().deleteAll()
    }
}