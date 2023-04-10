package com.reporter.common

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebView

@SuppressLint("SetJavaScriptEnabled")
fun createDynamicWebView(context: Context): WebView =
    WebView(context).also {
        it.settings.apply {
            javaScriptEnabled = true
        }
    }

fun WebView.loadContent(html: String) {
    this.loadDataWithBaseURL(
        null,
        html,
        Texts.MEME_TYPE_TEXT_HTML,
        Texts.UTF_8,
        null
    )
}