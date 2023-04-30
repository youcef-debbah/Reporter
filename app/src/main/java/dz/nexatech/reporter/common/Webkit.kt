package dz.nexatech.reporter.common

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.util.ui.AbstractApplication
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val DEFAULT_BASE_URL = "https://webkit/"

const val MIME_TYPE_ANY = "*/*"
const val MIME_TYPE_TEXT_PLAIN = "text/plain"

const val MIME_TYPE_TEXT_HTML = "text/html"
const val MIME_TYPE_APPLICATION_PDF = "application/pdf"
const val MIME_TYPE_SVG = "image/svg+xml"
const val MIME_TYPE_FONT_TTF = "font/ttf"
const val MIME_TYPE_TEXT_CSS = "text/css"
const val MIME_TYPE_TEXT_JAVASCRIPT = "text/javascript"
const val MIME_TYPE_APPLICATION_JSON = "application/json"
const val MIME_TYPE_APPLICATION_ZIP = "application/zip"

object Webkit {

    object TypesMappings {
        val mimeTypesByExtensions: ImmutableMap<String, String> = ImmutableMap.Builder<String, String>()
            .put(FILE_EXTENSION_PDF, MIME_TYPE_APPLICATION_PDF)
            .put(FILE_EXTENSION_SVG, MIME_TYPE_SVG)
            .put(FILE_EXTENSION_TTF, MIME_TYPE_FONT_TTF)
            .put(FILE_EXTENSION_CSS, MIME_TYPE_TEXT_CSS)
            .put(FILE_EXTENSION_JS, MIME_TYPE_TEXT_JAVASCRIPT)
            .put(FILE_EXTENSION_JSON, MIME_TYPE_APPLICATION_JSON)
            .put(FILE_EXTENSION_ZIP, MIME_TYPE_APPLICATION_ZIP)
            .build()
    }

    fun mimeType(filename: String) = mimeType(File(filename))

    fun mimeType(file: File): String = TypesMappings.mimeTypesByExtensions[file.extension]?: MIME_TYPE_TEXT_PLAIN

    object Formatters {
        val httpDateFormatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("GMT")
        }
    }

    fun formatDate(epoch: Long): String = formatDate(Date(epoch))

    fun formatDate(date: Date): String = Formatters.httpDateFormatter.format(date)
}

@SuppressLint("SetJavaScriptEnabled")
fun newDynamicWebView(
    context: Context = AbstractApplication.INSTANCE,
    config: WebSettings.() -> Unit = { javaScriptEnabled = true },
): WebView = WebView(context).also {
    it.settings.apply {
        javaScriptEnabled = true
    }.apply(config)
}

fun WebView.loadContent(html: String, baseUrl: String? = DEFAULT_BASE_URL) {
    loadDataWithBaseURL(
        baseUrl,
        html,
        MIME_TYPE_TEXT_HTML,
        Texts.UTF_8,
        null
    )
}