package dz.nexatech.reporter.client.model

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.common.mainLaunch
import dz.nexatech.reporter.client.common.readAsString
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.client.common.withMain
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
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
@Stable
class MainViewModel @Inject constructor(
    val resourcesRepository: ResourcesRepository,
    private val templatesRepository: TemplatesRepository,
    private val inputRepository: InputRepository,
) : ViewModel() {
    private val context = AbstractApplication.INSTANCE

    private var currentTabsContext: TabsContext? = null

    private val _templateImporting: MutableState<Int> = mutableStateOf(0)
    val templateImporting: State<Int> = _templateImporting

    // not stable but never called from composable fun
    val activeDestinations: DestinationsRegistry = DestinationsRegistry()

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
        val newContext = TabsContext(template)
        currentTabsContext = newContext
        newContext.loadTemplateAndNavigateToPreviewTab(this, navController)
    }

    fun templates(): StateFlow<Map<String, Template>?> = templatesRepository.templates

    suspend fun newTemplateState(meta: TemplateMeta) =
        TemplateState.from(meta, inputRepository)

    suspend fun loadTemplateMeta(templateName: String): TemplateMeta {
        var metaInput = ""
        withIO {
            metaInput = templatesRepository.loadTemplateMeta(templateName)?.readAsString() ?: ""
        }
        return TemplateMeta.from(templateName, metaInput, Teller, Localizer)
    }

    suspend fun compileTemplate(templateName: String): PebbleTemplate? =
        withIO { templatesRepository.compileTemplateBlocking(templateName) }

    fun importTemplate(uri: Uri, navController: NavHostController) {
        _templateImporting.value++
        ioLaunch {
            var pendingJob: Job? = null
            try {
                context.useInputStream(uri) {
                    val loaded = TemplateEncoder.readZipInput(it, Localizer)
                    val templates = loaded.first.map(Template::from)
                    val resources = loaded.second.map(Resource::from)
                    if (templates.isEmpty()) {
                        Toasts.launchLong(R.string.no_templates_found, context)
                    } else {
                        pendingJob = mainLaunch {
                            currentTabsContext?.clear(activeDestinations, navController)
                            currentTabsContext = null
                        }
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
            } finally {
                pendingJob?.join()
                withMain {
                    _templateImporting.value--
                }
            }
        }
    }
}