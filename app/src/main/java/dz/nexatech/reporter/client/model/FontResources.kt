package dz.nexatech.reporter.client.model

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSortedMap
import dz.nexatech.reporter.client.common.FILE_PATH_SEPARATOR
import dz.nexatech.reporter.client.common.MimeType
import dz.nexatech.reporter.client.core.AbstractBinaryResource
import java.util.NavigableMap

const val FONT_RESOURCE_PREFIX = "fonts/"

private val fontWeights: NavigableMap<Int, String> =
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
val fontAssetsResources: Map<String, AbstractBinaryResource> =
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
    FONT_RESOURCE_PREFIX + fontDir + fontWeights[weight] + ".ttf"

fun fontPaths(fontNames: Collection<String>): List<String> {
    val result = ArrayList<String>(fontNames.size * 2)
    fontNames.forEach {
        val fontDir = it.replace(" ", "") + FILE_PATH_SEPARATOR
        result.add(fontPath(fontDir, 200))
        result.add(fontPath(fontDir, 400))
    }
    return result
}