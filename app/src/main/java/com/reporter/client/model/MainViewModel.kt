package com.reporter.client.model

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.reporter.client.ui.TabsContext
import com.reporter.common.readAsString
import com.reporter.common.withIO
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    val resourcesRepository: ResourcesRepository,
    private val templatesRepository: TemplatesRepository,
    private val valuesDAO: Lazy<ValuesDAO>,
) : ViewModel() {

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
        withIO { templatesRepository.compileTemplate(templateName) }
}