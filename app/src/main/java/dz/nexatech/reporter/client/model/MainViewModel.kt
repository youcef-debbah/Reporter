package dz.nexatech.reporter.client.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.filterByClass
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.common.readAsString
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.client.core.AbstractValuesDAO
import dz.nexatech.reporter.client.core.TemplateEncoder
import dz.nexatech.reporter.client.ui.TabsContext
import dz.nexatech.reporter.util.model.Localizer
import dz.nexatech.reporter.util.model.Teller
import dz.nexatech.reporter.util.model.useInputStream
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.Toasts
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val resourcesRepository: ResourcesRepository,
    private val templatesRepository: TemplatesRepository,
    private val valuesDAO: Lazy<AbstractValuesDAO>,
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
        TemplateState.from(templateName, meta, valuesDAO::get)

    suspend fun loadTemplateMeta(templateName: String): TemplateMeta {
        var metaInput = ""
        withIO {
            metaInput = templatesRepository.loadTemplateMeta(templateName)?.readAsString() ?: ""
        }
        return TemplateMeta.from(templateName, metaInput, Teller, Localizer)
    }

    suspend fun compileTemplate(templateName: String) =
        withIO { templatesRepository.compileTemplateBlocking(templateName) }

    fun importTemplate(uri: Uri) {
        ioLaunch {
            try {
                context.useInputStream(uri) {
                    val loaded = TemplateEncoder.readZipInput(it, Localizer)
                    val templates = loaded.first.filterByClass(Template::class)
                    val resources = loaded.second.filterByClass(Resource::class)
                    if (templates.isNullOrEmpty()) {
                        Toasts.launchLong(R.string.no_templates_found, context)
                    } else {
                        templatesRepository.updateTemplates(templates, resources)
                        Toasts.launchShort(
                            context.getString(
                                R.string.templates_updated_successfully,
                                templates.size
                            ), context
                        )
                    }
                }
            } catch (e: Exception) {
                Teller.warn("template importing failure", e)
                Toasts.launchShort(R.string.templates_importing_failed, context)
            }
        }
    }
}