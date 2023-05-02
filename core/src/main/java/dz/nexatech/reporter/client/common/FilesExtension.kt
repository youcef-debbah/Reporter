package dz.nexatech.reporter.client.common

import com.google.common.collect.ImmutableMap
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val FILE_PATH_SEPARATOR = "/"

object FilesExtension {

    const val TXT = "txt"
    
    const val PROPERTIES = "properties"
    const val PDF = "pdf"
    const val SVG = "svg"
    const val TTF = "ttf"
    const val CSS = "css"
    const val JS = "js"
    const val JSON = "json"
    const val ZIP = "zip"
}

object MimeType {

    const val ANY = "*/*"
    const val TEXT_PLAIN = "text/plain"
    
    const val TEXT_HTML = "text/html"
    const val APPLICATION_PDF = "application/pdf"
    const val SVG = "image/svg+xml"
    const val FONT_TTF = "font/ttf"
    const val TEXT_CSS = "text/css"
    const val TEXT_JAVASCRIPT = "text/javascript"
    const val APPLICATION_JSON = "application/json"
    const val APPLICATION_ZIP = "application/zip"
    
    object TypesMappings {
        val mimeTypesByExtensions: ImmutableMap<String, String> = ImmutableMap.Builder<String, String>()
            .put(FilesExtension.PDF, APPLICATION_PDF)
            .put(FilesExtension.SVG, SVG)
            .put(FilesExtension.TTF, FONT_TTF)
            .put(FilesExtension.CSS, TEXT_CSS)
            .put(FilesExtension.JS, TEXT_JAVASCRIPT)
            .put(FilesExtension.JSON, APPLICATION_JSON)
            .put(FilesExtension.ZIP, APPLICATION_ZIP)
            .build()
    }

    fun of(filename: String) = of(File(filename))

    fun of(file: File): String = TypesMappings.mimeTypesByExtensions[file.extension]?: TEXT_PLAIN
}