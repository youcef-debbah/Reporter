@file:JvmName("FilesExtensionsKt")

package dz.nexatech.reporter.util.model

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import dz.nexatech.reporter.client.common.MimeType
import dz.nexatech.reporter.client.common.Texts
import dz.nexatech.reporter.util.ui.AbstractApplication

const val DEFAULT_BASE_URL = "https://webkit/"

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
        MimeType.TEXT_HTML,
        Texts.UTF_8,
        null
    )
}