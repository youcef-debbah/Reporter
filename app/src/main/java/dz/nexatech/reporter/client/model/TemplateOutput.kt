@file:OptIn(FlowPreview::class)

package dz.nexatech.reporter.client.model

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.compose.runtime.IntState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.FilesExtension
import dz.nexatech.reporter.client.common.MimeType
import dz.nexatech.reporter.client.common.backgroundLaunch
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.common.withMain
import dz.nexatech.reporter.client.core.PdfConverter
import dz.nexatech.reporter.client.ui.TabsContext
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.Teller
import dz.nexatech.reporter.util.model.useOutputStream
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.Toasts
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce


class TemplateOutput(
    private val resourcesRepository: ResourcesRepository,
    private val templateState: TemplateState,
    val htmlContent: MutableState<String>,
) {

    companion object {
        private val context = AbstractApplication.INSTANCE

        private val latestUri = mutableStateOf<Uri>(Uri.EMPTY)

        private val _pdfGenerating: MutableIntState = mutableIntStateOf(0)
        val pdfGenerating: IntState = _pdfGenerating

        fun from(
            tabsContext: TabsContext,
            resourcesRepository: ResourcesRepository,
            templateState: TemplateState,
            compiledTemplate: PebbleTemplate,
            initialContent: String,
        ): TemplateOutput {
            val htmlContent = mutableStateOf(initialContent)
            tabsContext.tabsScope.backgroundLaunch {
                htmlContent.value = compiledTemplate.evaluateState(templateState)

                templateState.lastUpdate
                    .debounce(AppConfig.get(TEMPLATE_PREVIEW_DEBOUNCE))
                    .collect {
                        htmlContent.value = compiledTemplate.evaluateState(templateState)
                    }
            }
            return TemplateOutput(
                resourcesRepository,
                templateState,
                htmlContent
            )
        }
    }

    val pdfConverter = PdfConverter(
        resourcesRepository,
        AppConfig.get(PDF_RESOURCES_CACHING_ENABLED),
        AppConfig.get(PDF_COMPRESSION_LEVEL),
    ) {
        resourcesRepository.loadFonts(templateState.fontsVariablesStates.map { it.state.value })
    }

    fun exportTemplateAsPDF(uri: Uri): Boolean {
        var result = false
        _pdfGenerating.intValue++
        ioLaunch {
            try {
                context.useOutputStream(uri) { outputStream ->
                    pdfConverter.generatePDF(
                        pageWidth(),
                        outputStream,
                        htmlContent.value
                    )
                    withMain {
                        Toasts.short(R.string.pdf_exporting_succeed, context)
                        latestUri.value = uri
                        result = true
                    }
                }
            } catch (e: Exception) {
                Teller.error("pdf exporting failed for path: ${uri.path}", e)
                withMain {
                    Toasts.short(R.string.pdf_exporting_internal_failure, context)
                    latestUri.value = Uri.EMPTY
                }
            } finally {
                withMain {
                    _pdfGenerating.intValue--
                }
            }
        }

        return result
    }

    private fun pageWidth(): Float? =
        (templateState.sectionsVariableStates["page_width"]?.state?.value)?.toFloatOrNull()

    fun newExportPdfIntent(): Intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = MimeType.APPLICATION_PDF
        putExtra(Intent.EXTRA_TITLE, "${templateState.templateName}.${FilesExtension.PDF}")
        if (latestUri != Uri.EMPTY && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, latestUri.value)
        }
    }
}