package com.reporter.client.model

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.reporter.client.R
import com.reporter.client.ui.TabsContext
import com.reporter.common.MIME_TYPE_APPLICATION_PDF
import com.reporter.common.backgroundLaunch
import com.reporter.common.ioLaunch
import com.reporter.common.useOutputStream
import com.reporter.util.model.AppConfig
import com.reporter.util.model.Teller
import com.reporter.util.ui.AbstractApplication
import com.reporter.util.ui.Toasts
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.flow.debounce


class TemplateOutput(
    private val resourcesRepository: ResourcesRepository,
    private val templateState: TemplateState,
    val htmlContent: MutableState<String>
) {

    private val context = AbstractApplication.INSTANCE

    val pdfConverter = PdfConverter(resourcesRepository) {
        resourcesRepository.loadFonts(templateState.fontsVariablesStates.values.map { it.state.value })
    }

    private val latestUri = mutableStateOf<Uri>(Uri.EMPTY)

    fun exportTemplateAsPDF(uri: Uri) {
        ioLaunch {
            try {
                context.useOutputStream(uri) { outputStream ->
                    pdfConverter.generatePDF(
                        outputStream,
                        htmlContent.value
                    )
                    Toasts.launchShort(R.string.pdf_exporting_succeed)
                    latestUri.value = uri
                }
            } catch (e: Exception) {
                Teller.error("pdf exporting failed for path: ${uri.path}", e)
                Toasts.launchShort(R.string.pdf_exporting_internal_failure)
            }
            latestUri.value = Uri.EMPTY
        }
    }

    fun newExportPdfIntent(): Intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = MIME_TYPE_APPLICATION_PDF
        putExtra(Intent.EXTRA_TITLE, "${templateState.template}.pdf")
        if (latestUri != Uri.EMPTY && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, latestUri.value)
        }
    }

    companion object {
        fun from(
            tabsContext: TabsContext,
            resourcesRepository: ResourcesRepository,
            templateState: TemplateState,
            compiledTemplate: PebbleTemplate,
            initialContent: String,
        ): TemplateOutput {
            val htmlContent = mutableStateOf(initialContent)
            tabsContext.tabsScope.backgroundLaunch {
                templateState.templateUpdates
                    .debounce(AppConfig.get(CONFIG_TEMPLATE_PREVIEW_DEBOUNCE))
                    .collect {
                        val html = compiledTemplate.evaluateState(templateState)
                        htmlContent.value = html
                    }
            }
            return TemplateOutput(
                resourcesRepository,
                templateState,
                htmlContent
            )
        }
    }
}