@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package dz.nexatech.reporter.client.ui

import android.content.res.Resources
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.createGraph
import com.google.accompanist.navigation.animation.composable
import com.google.common.collect.ImmutableList
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.AsyncConfig
import dz.nexatech.reporter.client.common.backgroundLaunch
import dz.nexatech.reporter.client.common.removeIf
import dz.nexatech.reporter.client.common.withMain
import dz.nexatech.reporter.client.model.MainViewModel
import dz.nexatech.reporter.client.model.Record
import dz.nexatech.reporter.client.model.RecordState
import dz.nexatech.reporter.client.model.ResourcesRepository
import dz.nexatech.reporter.client.model.Section
import dz.nexatech.reporter.client.model.SectionState
import dz.nexatech.reporter.client.model.Template
import dz.nexatech.reporter.client.model.TemplateOutput
import dz.nexatech.reporter.client.model.TemplateState
import dz.nexatech.reporter.client.model.asWebResourceResponse
import dz.nexatech.reporter.client.model.evaluateState
import dz.nexatech.reporter.util.model.loadContent
import dz.nexatech.reporter.util.model.newDynamicWebView
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.AbstractDestination
import dz.nexatech.reporter.util.ui.ContentCard
import dz.nexatech.reporter.util.ui.DecorativeIcon
import dz.nexatech.reporter.util.ui.DefaultNavigationBar
import dz.nexatech.reporter.util.ui.DestinationsRegistry
import dz.nexatech.reporter.util.ui.ErrorTheme
import dz.nexatech.reporter.util.ui.InfoIcon
import dz.nexatech.reporter.util.ui.PaddedColumn
import dz.nexatech.reporter.util.ui.PaddedRow
import dz.nexatech.reporter.util.ui.SimpleScaffold
import dz.nexatech.reporter.util.ui.StandardAppBar
import dz.nexatech.reporter.util.ui.ThemedText
import dz.nexatech.reporter.util.ui.VariableInput
import dz.nexatech.reporter.util.ui.contentPadding
import dz.nexatech.reporter.util.ui.stringRes
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
        R.drawable.baseline_warning_amber_24,
        "error",
    )

    private val cleared = AtomicBoolean()

    @Volatile
    private var previewTabRoute: String? = null

    fun navigablePreviewTabRoute(): String? = if (cleared.get()) null else previewTabRoute

    fun clear(destinationsRegistry: DestinationsRegistry, navController: NavHostController) {
        if (!cleared.getAndSet(true)) {
            // remove old screens
            navController.graph.iterator()
                .removeIf(postRemove = { destinationsRegistry.remove(it.route) }) {
                    it.route?.startsWith(TemplateTab.TEMPLATE_ROUTE_PREFIX) ?: false
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
                        viewModel.activeDestinations,
                        navController,
                        meta.errorCode,
                    )
                }
            } else {
                val templateStateJob = async { viewModel.newTemplateState(template.name, meta) }
                val compiledTemplateJob = async { viewModel.compileTemplate(template.name) }
                val templateState = templateStateJob.await()
                val compiledTemplate = compiledTemplateJob.await()
                if (compiledTemplate == null) {
                    withMain {
                        buildAndNavigateToErrorTab(
                            viewModel.activeDestinations,
                            navController,
                            500,
                        )
                    }
                } else {
                    val initialContent = compiledTemplate.evaluateState(templateState)
                    withMain {
                        buildAndNavigateToTemplatePreviewTab(
                            viewModel.activeDestinations,
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
        }
        buildAndNavigateToLoadingTab(viewModel.activeDestinations, navController)
    }

    private fun buildAndNavigateToLoadingTab(
        destinationsRegistry: DestinationsRegistry,
        navController: NavHostController
    ) {
        val newGraph =
            navController.createGraph(
                loadingTab.route,
                TemplateTab.TEMPLATE_ROUTE_PREFIX + "_loading"
            ) { buildLoadingTab(this, destinationsRegistry, navController) }
        navController.graph.addAll(newGraph)

        navController.navigate(loadingTab.route)
    }

    private fun buildLoadingTab(
        navGraphBuilder: NavGraphBuilder,
        destinationsRegistry: DestinationsRegistry,
        navController: NavHostController
    ) {
        destinationsRegistry.register(navGraphBuilder, navController) {
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

            loadingTab
        }
    }

    private fun buildAndNavigateToErrorTab(
        destinationsRegistry: DestinationsRegistry,
        navController: NavHostController,
        errorCode: Int,
    ) {
        val newGraph =
            navController.createGraph(
                errorTab.route,
                TemplateTab.TEMPLATE_ROUTE_PREFIX + "_error"
            ) {
                buildErrorTab(destinationsRegistry, this, navController, errorCode)
            }

        navController.graph.addAll(newGraph)
        navController.navigate(errorTab.route) {
            popUpTo(loadingTab.route) {
                inclusive = true
            }
        }
    }

    private fun buildErrorTab(
        destinationsRegistry: DestinationsRegistry,
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        errorCode: Int,
    ) {
        destinationsRegistry.register(navGraphBuilder, navController) {
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
                                DecorativeIcon(icon = R.drawable.baseline_warning_amber_24)
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
            errorTab
        }
    }

    private fun buildAndNavigateToTemplatePreviewTab(
        destinationsRegistry: DestinationsRegistry,
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
                TemplateTab.TEMPLATE_ROUTE_PREFIX + "_loaded"
            ) {
                buildPreviewTab(
                    this,
                    navController,
                    destinationsRegistry,
                    previewTab,
                    templateOutput,
                    webView,
                )

                for (pair in sectionTabs) {
                    val tab = pair.first
                    val sectionState = pair.second
                    val section = sectionState.section
                    buildSectionTab(
                        this,
                        navController,
                        destinationsRegistry,
                        tab,
                        section,
                        sectionState,
                        resourcesRepository
                    )
                }

                for (pair in recordsTabs) {
                    val tab = pair.first
                    val recordState = pair.second
                    val record = recordState.record
                    buildRecordTab(
                        this,
                        navController,
                        destinationsRegistry,
                        tab,
                        record,
                        recordState,
                        resourcesRepository,
                    )
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

    private fun buildRecordTab(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        destinationsRegistry: DestinationsRegistry,
        tab: TemplateTab,
        record: Record,
        recordState: RecordState,
        resourcesRepository: ResourcesRepository
    ) {
        destinationsRegistry.register(navGraphBuilder, navController) { controller ->
            composable(tab.route) {
                TabScaffold(destinationsRegistry, controller, tab) {
                    ThemedText("Record name:" + record.name)
                    ThemedText("Record label: " + record.label)
                    ThemedText("Record desc: " + record.desc)
                    for (variable in recordState.variables.values) {
                        VariableInput(variable, resourcesRepository)
                    }
                }
            }
            tab
        }
    }

    private fun buildSectionTab(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        destinationsRegistry: DestinationsRegistry,
        tab: TemplateTab,
        section: Section,
        sectionState: SectionState,
        resourcesRepository: ResourcesRepository,
    ) {
        destinationsRegistry.register(navGraphBuilder, navController) { controller ->
            composable(tab.route) {
                TabScaffold(destinationsRegistry, controller, tab) {
                    ThemedText("Section label: " + section.label)
                    ThemedText("Section desc: " + section.desc)
                    for (variable in sectionState.variables.values) {
                        VariableInput(variable, resourcesRepository)
                    }
                }
            }
            tab
        }
    }

    private fun buildPreviewTab(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        destinationsRegistry: DestinationsRegistry,
        previewTab: TemplateTab,
        templateOutput: TemplateOutput,
        webView: WebView
    ) {
        destinationsRegistry.register(navGraphBuilder, navController) { controller ->
            composable(previewTab.route) {
                val pdfExportingLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    result.data?.data?.let { uri ->
                        templateOutput.exportTemplateAsPDF(uri)
                    }
                }

                val html by templateOutput.htmlContent
                var toolbarExpanded by rememberSaveable { mutableStateOf(true) }

                SimpleScaffold(
                    topBar = {
                        Column {
                            StandardAppBar(controller, previewTab.title())
                            DefaultNavigationBar(controller, destinationsRegistry)
                        }
                    }) {
                    PaddedColumn {
                        ContentCard(
                            Modifier.clickable(
                                onClickLabel = stringRes(if (toolbarExpanded) R.string.collapse_template_preview_toolbar_desc else R.string.expand_template_preview_toolbar_desc),
                            ) {
                                toolbarExpanded = toolbarExpanded.not()
                            }
                        ) {
                            PaddedColumn {
                                AnimatedVisibility(toolbarExpanded) {
                                    ThemedText(previewTab.template.desc)
                                }
                                PaddedRow {
                                    Button(
                                        enabled = TemplateOutput.pdfGenerating.value == 0,
                                        onClick = {
                                            pdfExportingLauncher.launch(templateOutput.newExportPdfIntent())
                                        }) {
                                        if (TemplateOutput.pdfGenerating.value == 0) {
                                            DecorativeIcon(icon = R.drawable.baseline_picture_as_pdf_24)
                                        } else {
                                            CircularProgressIndicator(Modifier.size(24.dp))
                                        }
                                        ThemedText(R.string.export_pdf)
                                    }
                                    FilledIconButton({ webView.setInitialScale(0) }) {
                                        InfoIcon(
                                            icon = R.drawable.baseline_zoom_out_map_24,
                                            desc = R.string.zoom_in_preview_desc
                                        )
                                    }
                                    FilledIconButton({ webView.setInitialScale(1) }) {
                                        InfoIcon(
                                            icon = R.drawable.baseline_zoom_in_map_24,
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
            }
            previewTab
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
    TEMPLATE_ROUTE_PREFIX + template.name + '_' + tabName,
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
        const val TEMPLATE_ROUTE_PREFIX = "template_"
    }
}

@Composable
private fun TabScaffold(
    destinationsRegistry: DestinationsRegistry,
    navController: NavHostController,
    tab: TemplateTab,
    block: @Composable () -> Unit
) {
    SimpleScaffold(
        topBar = {
            Column {
                StandardAppBar(navController, tab.title())
                DefaultNavigationBar(navController, destinationsRegistry)
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