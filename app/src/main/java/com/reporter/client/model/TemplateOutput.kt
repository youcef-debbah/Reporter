package com.reporter.client.model

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.CompressionConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.font.FontProvider
import com.itextpdf.layout.font.FontSet
import com.reporter.client.R
import com.reporter.client.ui.TabsContext
import com.reporter.common.MIME_TYPE_APPLICATION_PDF
import com.reporter.common.backgroundLaunch
import com.reporter.common.ioLaunch
import com.reporter.util.model.AppConfig
import com.reporter.util.model.Teller
import com.reporter.util.ui.Toasts
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.flow.debounce


class TemplateOutput(
    private val resourcesRepository: ResourcesRepository,
    private val templateState: TemplateState,
    val htmlContent: MutableState<String>
) {

    private val latestUri = mutableStateOf<Uri>(Uri.EMPTY)

    private suspend fun loadFontSet() = FontSet().apply {
        val fontNames = templateState.fontsVariablesStates.values.map { it.state.value }
        resourcesRepository.loadFonts(fontNames).forEach { resource ->
            addFont(resource.asByteArray(), PdfEncodings.IDENTITY_H)
        }
    }

    private suspend fun buildConverterProperties(): ConverterProperties =
        ConverterProperties().apply {
            resourceRetriever = resourcesRepository
            isImmediateFlush = false
            fontProvider = FontProvider(loadFontSet(), "Helvetica")
        }

    fun exportTemplateAsPDF(uri: Uri) {
        ioLaunch {
            try {
                resourcesRepository.openFile(uri)?.use { outputStream ->
                    val pdfWriter = PdfWriter(outputStream).apply {
                        compressionLevel = CompressionConstants.BEST_COMPRESSION
                        setSmartMode(AppConfig.get(CONFIG_PDF_RESOURCES_CACHING_ENABLED))
                    }

                    PdfDocument(pdfWriter).apply {
                        defaultPageSize = PageSize.A4
                    }.use { pdfDocument ->
                        val document = HtmlConverter.convertToDocument(
                            htmlContent.value,
                            pdfDocument,
                            buildConverterProperties()
                        )
                        document.setMargins(0f, 0f, 0f, 0f)
                        document.relayout()
                        document.flush()
                    }

                    Toasts.launchShort(R.string.pdf_exporting_succeed)
                    latestUri.value = uri
                    return@ioLaunch
                }
                Toasts.launchShort(R.string.pdf_exporting_system_failure)
                Teller.warn("null file output stream for path: ${uri.path}")
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