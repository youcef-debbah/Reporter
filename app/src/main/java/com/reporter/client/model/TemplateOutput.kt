package com.reporter.client.model

import android.webkit.WebView
import com.reporter.common.backgroundLaunch
import com.reporter.common.loadContent
import com.reporter.common.withMain
import com.reporter.util.model.AppConfig
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce

class TemplateOutput(val htmlContent: MutableStateFlow<String>) {

    companion object {
        fun from(
            templateState: TemplateState,
            compiledTemplate: PebbleTemplate,
            initialContent: String,
            webView: WebView,
            scope: CoroutineScope,
        ): TemplateOutput {
            val htmlContent = MutableStateFlow(initialContent)
            scope.backgroundLaunch {
                templateState.templateUpdates
                    .debounce(AppConfig.get(CONFIG_TEMPLATE_PREVIEW_DEBOUNCE))
                    .collect {
                        val html = compiledTemplate.evaluateState(templateState)
                        htmlContent.value = html
                        withMain {
                            webView.loadContent(html)
                        }
                    }
            }
            return TemplateOutput(htmlContent)
        }
    }
}