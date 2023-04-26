package com.reporter.common

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import com.google.common.collect.ImmutableMap
import com.reporter.util.ui.AbstractApplication
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val DEFAULT_BASE_URL = "https://webkit/"

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
            .put("pdf", MIME_TYPE_APPLICATION_PDF)
            .put("svg", MIME_TYPE_SVG)
            .put("ttf", MIME_TYPE_FONT_TTF)
            .put("css", MIME_TYPE_TEXT_CSS)
            .put("js", MIME_TYPE_TEXT_JAVASCRIPT)
            .put("json", MIME_TYPE_APPLICATION_JSON)
            .put("zip", MIME_TYPE_APPLICATION_ZIP)
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