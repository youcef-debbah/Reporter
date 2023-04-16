package com.reporter.common

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import com.reporter.util.ui.AbstractApplication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val MIME_TYPE_TEXT_HTML = "text/html"
const val MIME_TYPE_APPLICATION_PDF = "application/pdf"
const val MIME_TYPE_SVG = "image/svg+xml"
const val MIME_TYPE_TTF_FONT = "font/ttf"
const val DEFAULT_BASE_URL = "https://webkit/"

object Webkit {
    val httpDateFormatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).apply {
        timeZone = java.util.TimeZone.getTimeZone("GMT")
    }

    fun formatDate(epoch: Long): String = formatDate(Date(epoch))

    fun formatDate(date: Date): String = httpDateFormatter.format(date)
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