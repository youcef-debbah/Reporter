package dz.nexatech.reporter.client.model

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.ui.TabsContext
import dz.nexatech.reporter.common.MIME_TYPE_APPLICATION_PDF
import dz.nexatech.reporter.common.backgroundLaunch
import dz.nexatech.reporter.common.ioLaunch
import dz.nexatech.reporter.common.useOutputStream
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.Teller
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.Toasts
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