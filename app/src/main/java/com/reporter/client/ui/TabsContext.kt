@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.reporter.client.ui

import android.content.res.Resources
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.createGraph
import com.google.accompanist.navigation.animation.composable
import com.google.common.collect.ImmutableList
import com.reporter.client.R
import com.reporter.client.model.MainViewModel
import com.reporter.client.model.RecordState
import com.reporter.client.model.ResourcesRepository
import com.reporter.client.model.SectionState
import com.reporter.client.model.Template
import com.reporter.client.model.TemplateOutput
import com.reporter.client.model.TemplateState
import com.reporter.client.model.VariableState
import com.reporter.client.model.evaluateState
import com.reporter.common.AsyncConfig
import com.reporter.common.backgroundLaunch
import com.reporter.common.loadContent
import com.reporter.common.newDynamicWebView
import com.reporter.common.removeIf
import com.reporter.common.withMain
import com.reporter.util.ui.AbstractApplication
import com.reporter.util.ui.AbstractDestination
import com.reporter.util.ui.ContentCard
import com.reporter.util.ui.DecorativeIcon
import com.reporter.util.ui.DefaultNavigationBar
import com.reporter.util.ui.ErrorTheme
import com.reporter.util.ui.InfoIcon
import com.reporter.util.ui.PaddedColumn
import com.reporter.util.ui.PaddedRow
import com.reporter.util.ui.SimpleAppBar
import com.reporter.util.ui.SimpleScaffold
import com.reporter.util.ui.ThemedText
import com.reporter.util.ui.activeScreens
import com.reporter.util.ui.contentPadding
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import java.util.concurrent.atomic.AtomicBoolean


class TabsContext(val template: Template) {

    val context: AbstractApplication = AbstractApplication.INSTANCE
    val resources: Resources = context.resources
    val tabsScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + AsyncConfig.backgroundDispatcher)
    val loadingScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + AsyncConfig.backgroundDispatcher)

    private val loadingTab = TemplateTab(
        template,
        resources.getString(R.string.template_tab_loading_title, template.label),
        resources.getString(R.string.template_tab_loading_label),
        R.drawable.baseline_downloading_24,
        "loading",
    )

    private val errorTab = TemplateTab(
        template,
        resources.getString(R.string.template_tab_error_title),
        null,
        R.drawable.baseline_error_24,
        "error",
    )

    private val cleared = AtomicBoolean()

    @Volatile
    private var previewTabRoute: String? = null

    fun navigablePreviewTabRoute(): String? = if (cleared.get()) null else previewTabRoute

    fun clear(navController: NavHostController) {
        if (!cleared.getAndSet(true)) {
            // remove old screens
            navController.graph.iterator()
                .removeIf(postRemove = { activeScreens.remove(it.route) }) {
                    it.route?.startsWith(TemplateTab.GLOBAL_ROUTE_PREFIX) ?: false
                }
            tabsScope.cancel()
        }
    }

    fun loadTemplateAndNavigateToPreviewTab(
        viewModel: MainViewModel,
        navController: NavHostController
    ) {
        loadingScope.backgroundLaunch {
            val meta = viewModel.loadTemplateMeta(template.name)
            if (meta.hasErrors()) {
                withMain {
                    buildAndNavigateToErrorTab(
                        template,
                        meta.errorCode,
                        navController,
                    )
                }
            } else {
                val templateStateJob = async { viewModel.newTemplateState(template.name, meta) }
                val compiledTemplateJob = async { viewModel.compileTemplate(template.name) }
                val templateState = templateStateJob.await()
                val compiledTemplate = compiledTemplateJob.await()
                val initialContent = compiledTemplate.evaluateState(templateState)
                withMain {
                    buildAndNavigateToTemplatePreviewTab(
                        viewModel.resourcesRepository,
                        template,
                        templateState,
                        compiledTemplate,
                        initialContent,
                        navController,
                    )
                }
            }
        }
        buildAndNavigateToLoadingTab(navController)
    }

    private fun buildAndNavigateToLoadingTab(
        navController: NavHostController
    ) {
        val newGraph =
            navController.createGraph(
                loadingTab.route,
                TemplateTab.GLOBAL_ROUTE_PREFIX + "_loading"
            ) {
                activeScreens[loadingTab.route] = loadingTab
                composable(loadingTab.route) {
                    DisposableEffect(template) {
                        onDispose {
                            loadingScope.cancel()
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(Modifier.contentPadding())
                        ThemedText(R.string.template_tab_loading_desc)
                    }
                }
            }

        navController.graph.addAll(newGraph)
        navController.navigate(loadingTab.route)
    }

    private fun buildAndNavigateToErrorTab(
        template: Template,
        errorCode: Int,
        navController: NavHostController,
    ) {
        val newGraph =
            navController.createGraph(
                errorTab.route,
                TemplateTab.GLOBAL_ROUTE_PREFIX + "_error"
            ) {
                activeScreens[errorTab.route] = errorTab
                composable(errorTab.route) {
                    ErrorTheme {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Card {
                                Column(
                                    modifier = Modifier.contentPadding(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    DecorativeIcon(icon = R.drawable.baseline_error_24)
                                    ThemedText(
                                        resources.getString(
                                            R.string.template_tab_error_desc,
                                            template.label
                                        )
                                    )
                                    ThemedText(resources.getString(R.string.error_code, errorCode))
                                }
                            }
                        }
                    }
                }
            }

        navController.graph.addAll(newGraph)
        navController.navigate(errorTab.route) {
            popUpTo(loadingTab.route) {
                inclusive = true
            }
        }
    }

    private fun buildAndNavigateToTemplatePreviewTab(
        resourcesRepository: ResourcesRepository,
        template: Template,
        templateState: TemplateState,
        compiledTemplate: PebbleTemplate,
        initialContent: String,
        navController: NavHostController,
    ) {
        val tabsBuilder: ImmutableList.Builder<AbstractDestination> = ImmutableList.builder()
        val previewTab = TemplateTab(
            template,
            resources.getString(R.string.template_tab_preview_title, template.label),
            resources.getString(R.string.template_tab_preview_label),
            R.drawable.baseline_preview_24,
            "preview",
            tabsBuilder
        )
        tabsBuilder.add(previewTab)

        val sectionTabs =
            ArrayList<Pair<TemplateTab, SectionState>>(templateState.sectionStates.size)
        for ((i, sectionState) in templateState.sectionStates.withIndex()) {
            val section = sectionState.section
            val tab = TemplateTab(
                template,
                section.label,
                section.label,
                R.drawable.baseline_table_rows_24,
                "section_$i",
                tabsBuilder,
            )
            tabsBuilder.add(tab)
            sectionTabs.add(Pair(tab, sectionState))
        }

        val recordsTabs =
            ArrayList<Pair<TemplateTab, RecordState>>(templateState.recordsStates.size)
        for (recordState in templateState.recordsStates.values) {
            val record = recordState.record
            val tab = TemplateTab(
                template,
                record.label,
                record.label,
                R.drawable.baseline_table_rows_24,
                "record_" + record.name,
                tabsBuilder,
            )
            tabsBuilder.add(tab)
            recordsTabs.add(Pair(tab, recordState))
        }

        val templateOutput = TemplateOutput.from(
            this,
            resourcesRepository,
            templateState,
            compiledTemplate,
            initialContent,
        )

        val webView = newDynamicWebView(context) {
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? =
                resourcesRepository.loadBlocking(request?.url?.path)?.asWebResourceResponse()
        }

        val newGraph =
            navController.createGraph(
                previewTab.route,
                TemplateTab.GLOBAL_ROUTE_PREFIX + "_loaded"
            ) {
                activeScreens[previewTab.route] = previewTab
                composable(previewTab.route) {
                    val openDocumentLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        result.data?.data?.let { uri ->
                            templateOutput.exportTemplateAsPDF(uri)
                        }
                    }

                    val html by templateOutput.htmlContent

                    SimpleScaffold(
                        topBar = {
                            Column {
                                SimpleAppBar(previewTab.title())
                                DefaultNavigationBar(navController)
                            }
                        },
                        bottomBar = {
                        }) {
                        ContentCard {
                            PaddedColumn {
                                ThemedText(previewTab.template.desc)
                                PaddedRow {
                                    Button({
                                        openDocumentLauncher.launch(templateOutput.newExportPdfIntent())
                                    }) {
                                        ThemedText("export PDF")
                                    }
                                    Button({
                                        webView.setInitialScale(0)
                                    }) {
                                        InfoIcon(
                                            icon = R.drawable.baseline_zoom_in_24,
                                            desc = R.string.zoom_in_preview_desc
                                        )
                                    }
                                    Button({
                                        webView.setInitialScale(1)
                                    }) {
                                        InfoIcon(
                                            icon = R.drawable.baseline_zoom_out_24,
                                            desc = R.string.zoom_out_preview_desc
                                        )
                                    }
                                }
                            }
                        }

                        ContentCard(shape = RectangleShape) {
                            AndroidView(
                                factory = { webView },
                                update = { view -> view.loadContent(html) },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                for (pair in sectionTabs) {
                    val tab = pair.first
                    val sectionState = pair.second
                    val section = sectionState.section
                    activeScreens[tab.route] = tab
                    composable(tab.route) {
                        TabScaffold(tab = tab, navController = navController) {
                            ThemedText("Section label: " + section.label)
                            ThemedText("Section desc: " + section.desc)
                            for (variable in sectionState.variables.values) {
                                VariableInput(variable)
                            }
                        }
                    }
                }

                for (pair in recordsTabs) {
                    val tab = pair.first
                    val recordState = pair.second
                    val record = recordState.record
                    activeScreens[tab.route] = tab
                    composable(tab.route) {
                        TabScaffold(tab = tab, navController = navController) {
                            ThemedText("Record name:" + record.name)
                            ThemedText("Record label: " + record.label)
                            ThemedText("Record desc: " + record.desc)
                            for (variable in recordState.variables.values) {
                                VariableInput(variable)
                            }
                        }
                    }
                }
            }

        navController.graph.addAll(newGraph)
        previewTabRoute = previewTab.route
        navController.navigate(previewTab.route) {
            popUpTo(loadingTab.route) {
                inclusive = true
            }
        }
    }
}

@Immutable
private class TemplateTab(
    val template: Template,
    val title: String,
    val label: String?,
    @DrawableRes tabIcon: Int,
    tabName: String,
    tabsBuilder: ImmutableList.Builder<AbstractDestination> = ImmutableList.builder(),
) : AbstractDestination(
    GLOBAL_ROUTE_PREFIX + template.name + '_' + tabName,
    tabIcon,
) {

    @Composable
    override fun title() = title

    @Composable
    override fun label() = label

    override val destinations: List<AbstractDestination> by lazy {
        tabsBuilder.build()
    }

    companion object {
        const val GLOBAL_ROUTE_PREFIX = "template_"
    }
}

@Composable
private fun TabScaffold(
    tab: TemplateTab,
    navController: NavHostController,
    block: @Composable () -> Unit
) {
    SimpleScaffold(
        topBar = {
            Column {
                SimpleAppBar(tab.title())
                DefaultNavigationBar(navController)
            }
        },
        bottomBar = {
        }) {
        ContentCard {
            PaddedColumn {
                block()
            }
        }
    }
}

@Composable
fun VariableInput(variableState: VariableState) {
    val value by variableState.state
    PaddedColumn {
        Divider()
        ThemedText(variableState.variable.label)
        TextField(
            value = value,
            onValueChange = variableState.setter,
            label = { ThemedText(variableState.variable.label) },
        )
    }
}
