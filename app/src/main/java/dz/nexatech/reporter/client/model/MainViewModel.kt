package dz.nexatech.reporter.client.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.ui.TabsContext
import dz.nexatech.reporter.common.FILE_EXTENSION_PROPERTIES
import dz.nexatech.reporter.common.Webkit
import dz.nexatech.reporter.common.ioLaunch
import dz.nexatech.reporter.common.readAsBytes
import dz.nexatech.reporter.common.readAsString
import dz.nexatech.reporter.common.useInputStream
import dz.nexatech.reporter.common.withIO
import dz.nexatech.reporter.util.model.Teller
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.Toasts
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import java.io.ByteArrayInputStream
import java.util.LinkedList
import java.util.Properties
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject

private const val TEMPLATE_INFO_EXTENSION = ".$FILE_EXTENSION_PROPERTIES"

@HiltViewModel
class MainViewModel @Inject constructor(
    val resourcesRepository: ResourcesRepository,
    private val templatesRepository: TemplatesRepository,
    private val valuesDAO: Lazy<ValuesDAO>,
) : ViewModel() {
    private val context = AbstractApplication.INSTANCE

    private var currentTabsContext: TabsContext? = null

    fun navigateToTemplateTabs(
        template: Template,
        navController: NavHostController,
    ) {
        val oldContext = currentTabsContext
        if (oldContext?.template?.name == template.name) {
            val route = oldContext.navigablePreviewTabRoute()
            if (route != null) {
                navController.navigate(route)
                return
            }
        }

        oldContext?.clear(navController)
        val newContext = TabsContext(template)
        currentTabsContext = newContext
        newContext.loadTemplateAndNavigateToPreviewTab(this, navController)
    }

    fun templates(): StateFlow<Map<String, Template>?> = templatesRepository.templates

    suspend fun newTemplateState(templateName: String, meta: TemplateMeta) =
        TemplateState.from(templateName, meta, valuesDAO)

    suspend fun loadTemplateMeta(templateName: String): TemplateMeta {
        var metaInput = ""
        withIO {
            metaInput = templatesRepository.loadTemplateMeta(templateName)?.readAsString() ?: ""
        }
        return TemplateMeta.from(templateName, metaInput)
    }

    suspend fun compileTemplate(templateName: String) =
        withIO { templatesRepository.compileTemplateBlocking(templateName) }

    fun importTemplate(uri: Uri) {
        ioLaunch {
            try {
                context.useInputStream(uri) {
                    ZipInputStream(it).use { zipStream ->
                        val templates = LinkedList<Template>()
                        val resources = LinkedList<Resource>()
                        var entry: ZipEntry? = zipStream.nextEntry
                        while (entry != null) {
                            val entryName = entry.name
                            Teller.test("zip entry: $entryName")
                            if (entry.isDirectory.not()) {
                                if (entryName.endsWith(TEMPLATE_INFO_EXTENSION)) {
                                    val properties = Properties()
                                    properties.load(
                                        ByteArrayInputStream(
                                            zipStream.readAsBytes(
                                                entry.size.toInt(),
                                                false
                                            )
                                        )
                                    )
                                    val templateName: String? =
                                        properties.getProperty(TEMPLATE_COLUMN_NAME)
                                    if (templateName != null) {
                                        templates.add(
                                            Template(
                                                templateName,
                                                label_en = properties.getProperty(
                                                    TEMPLATE_COLUMN_LABEL_EN
                                                ),
                                                label_ar = properties.getProperty(
                                                    TEMPLATE_COLUMN_LABEL_AR
                                                ),
                                                label_fr = properties.getProperty(
                                                    TEMPLATE_COLUMN_LABEL_FR
                                                ),
                                                desc_en = properties.getProperty(
                                                    TEMPLATE_COLUMN_DESC_EN
                                                ),
                                                desc_ar = properties.getProperty(
                                                    TEMPLATE_COLUMN_DESC_AR
                                                ),
                                                desc_fr = properties.getProperty(
                                                    TEMPLATE_COLUMN_DESC_FR
                                                ),
                                                System.currentTimeMillis(),
                                            )
                                        )
                                    }
                                } else {
                                    resources.add(
                                        Resource(
                                            path = entryName,
                                            mimeType = Webkit.mimeType(entryName),
                                            data = zipStream.readAsBytes(entry.size.toInt(), false),
                                            lastModified = System.currentTimeMillis(),
                                        )
                                    )
                                }
                            }
                            entry = zipStream.nextEntry
                        }
                        if (templates.isEmpty()) {
                            Toasts.launchLong(R.string.no_templates_found, context)
                        } else {
                            templatesRepository.updateTemplates(templates, resources)
                            Toasts.launchShort(context.getString(R.string.templates_updated_successfully, templates.size), context)
                        }
                    }
                }
            } catch (e: Exception) {
                Teller.warn("template importing failure", e)
                Toasts.launchShort(R.string.templates_importing_failed, context)
            }
        }
    }
}