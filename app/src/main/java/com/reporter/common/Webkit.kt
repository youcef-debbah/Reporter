package com.reporter.common

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import com.reporter.util.ui.AbstractApplication

@SuppressLint("SetJavaScriptEnabled")
fun newDynamicWebView(
    context: Context = AbstractApplication.INSTANCE,
    config: WebSettings.() -> Unit = { javaScriptEnabled = true },
): WebView = WebView(context).also {
    it.settings.apply {
            javaScriptEnabled = true
        }.apply(config)
}

fun WebView.loadContent(html: String, baseUrl: String? = Texts.FILE_PROTOCOL_ASSETS_PREFIX) {
    this.loadDataWithBaseURL(
        baseUrl,
        html,
        Texts.MEME_TYPE_TEXT_HTML,
        Texts.UTF_8,
        null
    )
}