package dz.nexatech.reporter.client.model

import android.net.Uri
import androidx.compose.runtime.IntState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.common.mainLaunch
import dz.nexatech.reporter.client.common.readAsString
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.client.common.withMain
import dz.nexatech.reporter.client.core.AbstractBinaryResource
import dz.nexatech.reporter.client.core.AbstractTemplate
import dz.nexatech.reporter.client.core.TemplateEncoder
import dz.nexatech.reporter.client.ui.TabsContext
import dz.nexatech.reporter.util.model.Localizer
import dz.nexatech.reporter.util.model.Teller
import dz.nexatech.reporter.util.model.useInputStream
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.DestinationsRegistry
import dz.nexatech.reporter.util.ui.Toasts
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val resourcesRepository: ResourcesRepository,
    private val templatesRepository: TemplatesRepository,
    private val inputRepository: InputRepository,
) : ViewModel() {
    private val context = AbstractApplication.INSTANCE

    @Volatile
    private var currentTabsContext: TabsContext? = null

    val activeDestinations: DestinationsRegistry = DestinationsRegistry()

    private val _templateImporting: MutableIntState = mutableIntStateOf(0)
    val templateImporting: IntState = _templateImporting

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

        oldContext?.clear(activeDestinations, navController)
        val newContext = TabsContext(template, templatesRepository)
        currentTabsContext = newContext
        newContext.loadTemplateAndNavigateToPreviewTab(this, navController)
    }

    val templates: State<Map<String, Template>?> = templatesRepository.templates

    suspend fun newTemplateState(meta: TemplateMeta) =
        TemplateState.from(meta, inputRepository)

    suspend fun loadTemplateMeta(template: Template): TemplateMeta {
        val templateName = template.name
        var metaInput = ""
        withIO {
            metaInput = templatesRepository.loadTemplateMeta(templateName)?.readAsString() ?: ""
        }
        return TemplateMeta.from(templateName, metaInput, Teller, Localizer.from(template.lang))
    }

    suspend fun compileTemplate(templateName: String): PebbleTemplate? =
        withIO { templatesRepository.compileTemplateBlocking(templateName) }

    fun importTemplate(uri: Uri, navController: NavHostController) {
        _templateImporting.intValue++
        ioLaunch {
            var pendingJob: Job? = null
            try {
                context.useInputStream(uri) {
                    val loaded: Pair<List<AbstractTemplate>, List<AbstractBinaryResource>> =
                        TemplateEncoder.readZipInput(it, Localizer::from)

                    val templates = loaded.first.map(Template::from)
                    val resources = loaded.second.map(Resource::from)
                    val size = templates.size
                    if (size == 0) {
                        Toasts.launchLong(R.string.no_templates_found, context)
                    } else {
                        pendingJob = mainLaunch {
                            currentTabsContext?.clear(activeDestinations, navController)
                            currentTabsContext = null
                        }
                        templatesRepository.updateTemplates(templates, resources)
                        Toasts.launchShort(
                            context.resources.getQuantityString(R.plurals.templates_updated_successfully, size, size),
                            context
                        )
                    }
                }
            } catch (e: Exception) {
                Teller.warn("template importing failure", e)
                Toasts.launchShort(R.string.templates_importing_failed, context)
            } finally {
                pendingJob?.join()
                withMain {
                    _templateImporting.intValue--
                }
            }
        }
    }

    fun deleteAllTemplates() {
        mainLaunch {
            templatesRepository.deleteAllTemplates()
        }
    }
}