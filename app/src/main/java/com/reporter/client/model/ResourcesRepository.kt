package com.reporter.client.model

import android.content.Context
import android.net.Uri
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSortedMap
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever
import com.reporter.common.AsyncConfig
import com.reporter.common.MIME_TYPE_TTF_FONT
import com.reporter.common.withIO
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

private const val FONT_DIR_PATH = "fonts/"
private const val FONT_FILE_FORMAT = ".ttf"

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
        put(path, AssetResource(path, MIME_TYPE_TTF_FONT))
    }
    return this
}

fun fontPath(fontDir: String, weight: Int) =
    FONT_DIR_PATH + fontDir + FONT_WEIGHTS[weight] + FONT_FILE_FORMAT

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

    suspend fun loadFonts(fontNames: Collection<String>): List<BinaryResource> = withIO {
        if (fontNames.isEmpty()) {
            return@withIO emptyList()
        } else if (fontNames.size == 1) {
            return@withIO loadAll(fontPaths(fontNames))
        } else {
            return@withIO loadAll(fontPaths(LinkedHashSet(fontNames)))
        }
    }

    suspend fun loadAll(paths: List<String>): List<BinaryResource> = withIO {
        paths.map { path ->
            async { load(path) }
        }.awaitAll().filterNotNull()
    }

    suspend fun load(path: String?): BinaryResource? = withIO {
        path?.let { loadBinaryResource(it.removePrefix("/")) }
    }

    private fun loadBinaryResource(path: String): BinaryResource? {
        val font = FONT_ASSETS[path]//tofix
        if (font != null) {
            return font
        }

        if (path.startsWith("static")) {
            return AssetResource(path, "text/css")
        }

        val resource = resourcesDAO.get().load(path)
        if (resource != null) {
            Teller.debug("resource loaded: $path")
            return resource
        } else {
            Teller.warn("resource not found: $path")
            return null
        }
    }

    fun loadBlocking(path: String?): BinaryResource? = runBlocking(AsyncConfig.ioDispatcher) {
        load(path)
    }

    override fun getInputStreamByUrl(url: URL): InputStream? =
        loadBlocking(url.path)?.asInputStream()

    override fun getByteArrayByUrl(url: URL): ByteArray? = loadBlocking(url.path)?.asByteArray()

    suspend fun openSystemContent(uri: Uri): OutputStream? = withIO {
        context.contentResolver.openOutputStream(uri)
    }
}