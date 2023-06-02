@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package dz.nexatech.reporter.client.ui

import android.content.res.Resources
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.google.common.collect.ImmutableMap
import dagger.hilt.android.internal.ThreadUtil
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.AsyncConfig
import dz.nexatech.reporter.client.common.backgroundLaunch
import dz.nexatech.reporter.client.common.removeIf
import dz.nexatech.reporter.client.common.slice
import dz.nexatech.reporter.client.common.withMain
import dz.nexatech.reporter.client.model.MAX_LAYOUT_COLUMN_WIDTH
import dz.nexatech.reporter.client.model.MainViewModel
import dz.nexatech.reporter.client.model.Record
import dz.nexatech.reporter.client.model.RecordState
import dz.nexatech.reporter.client.model.ResourcesRepository
import dz.nexatech.reporter.client.model.Section
import dz.nexatech.reporter.client.model.SectionState
import dz.nexatech.reporter.client.model.Template
import dz.nexatech.reporter.client.model.TemplateOutput
import dz.nexatech.reporter.client.model.TemplateState
import dz.nexatech.reporter.client.model.VariableState
import dz.nexatech.reporter.client.model.asWebResourceResponse
import dz.nexatech.reporter.client.model.evaluateState
import dz.nexatech.reporter.util.model.loadContent
import dz.nexatech.reporter.util.model.newDynamicWebView
import dz.nexatech.reporter.util.model.rememberColumnsCount
import dz.nexatech.reporter.util.model.rememberDpState
import dz.nexatech.reporter.util.model.rememberMaxLayoutColumnWidth
import dz.nexatech.reporter.util.model.stringToStringSnapshotStateMapSaver
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.AbstractDestination
import dz.nexatech.reporter.util.ui.AbstractIcon
import dz.nexatech.reporter.util.ui.Body
import dz.nexatech.reporter.util.ui.CentredColumn
import dz.nexatech.reporter.util.ui.CentredRow
import dz.nexatech.reporter.util.ui.ContentCard
import dz.nexatech.reporter.util.ui.DecorativeIcon
import dz.nexatech.reporter.util.ui.DefaultNavigationBar
import dz.nexatech.reporter.util.ui.DestinationsRegistry
import dz.nexatech.reporter.util.ui.ErrorTheme
import dz.nexatech.reporter.util.ui.InfoIcon
import dz.nexatech.reporter.util.ui.Line
import dz.nexatech.reporter.util.ui.LocalDimens
import dz.nexatech.reporter.util.ui.PaddedColumn
import dz.nexatech.reporter.util.ui.PaddedDivider
import dz.nexatech.reporter.util.ui.ScrollableColumn
import dz.nexatech.reporter.util.ui.SimpleScaffold
import dz.nexatech.reporter.util.ui.StandardAppBar
import dz.nexatech.reporter.util.ui.StaticIcon
import dz.nexatech.reporter.util.ui.Theme
import dz.nexatech.reporter.util.ui.Title
import dz.nexatech.reporter.util.ui.VariableInput
import dz.nexatech.reporter.util.ui.contentPadding
import dz.nexatech.reporter.util.ui.iconsAssetsResources
import dz.nexatech.reporter.util.ui.stringRes
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel


class TabsContext(val template: Template) {

    companion object {
        val context: AbstractApplication = AbstractApplication.INSTANCE
        val resources: Resources = context.resources
    }

    private val loadingTab = TemplateTab(
        template,
        resources.getString(R.string.template_tab_loading_title, template.label),
        resources.getString(R.string.template_tab_loading_label),
        StaticIcon.baseline_downloading,
        "loading",
    )

    private val errorTab = TemplateTab(
        template,
        resources.getString(R.string.template_tab_error_title),
        null,
        StaticIcon.baseline_warning,
        "error",
    )

    val previewTabLabel = resources.getString(R.string.template_tab_preview_label)
    val previewTabIcon = StaticIcon.baseline_preview

    val tabsScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + AsyncConfig.backgroundDispatcher)
    val loadingScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + AsyncConfig.backgroundDispatcher)

    private var cleared: Boolean = false
    private var previewTabRoute: String? = null

    fun navigablePreviewTabRoute(): String? = if (cleared) null else previewTabRoute

    fun clear(destinationsRegistry: DestinationsRegistry, navController: NavHostController) {
        ThreadUtil.ensureMainThread()
        if (!cleared) {
            cleared = true
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
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Body(R.string.template_tab_loading_desc, Modifier.contentPadding())
                    CircularProgressIndicator(Modifier.contentPadding())
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
                                modifier = Modifier.padding(Theme.dimens.content_padding * 2),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                DecorativeIcon(
                                    icon = R.drawable.baseline_warning_24,
                                    modifier = Modifier.size(Theme.dimens.main_icon_size)
                                )
                                Body(R.string.template_tab_error_desc)
                                Title(template.label)
                                Body(resources.getString(R.string.error_code, errorCode))
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
            previewTabLabel,
            previewTabIcon,
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
                tabIcon(section.icon),
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
                tabIcon(record.icon),
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
    ) {
        destinationsRegistry.register(navGraphBuilder, navController) { controller ->
            composable(tab.route) {
                TabScaffold(destinationsRegistry, controller, tab) {
                    Body("Record name:" + record.name)
                    Body("Record label: " + record.label)
                    Body("Record desc: " + record.desc)
                    VariablesRows(recordState.variables, tab)
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
    ) {
        destinationsRegistry.register(navGraphBuilder, navController) { controller ->
            composable(tab.route) {
                TabScaffold(destinationsRegistry, controller, tab) {
                    Title(section.desc, Modifier.contentPadding())
                    VariablesRows(sectionState.variables, tab)
                }
            }
            tab
        }
    }

    @Composable
    private fun VariablesRows(
        variables: ImmutableMap<String, VariableState>,
        tab: TemplateTab,
    ) {
        CentredColumn(
            Modifier.padding(
                start = Theme.dimens.content_padding.start,
                end = Theme.dimens.content_padding.end,
            )
        ) {
            val columnsCount by rememberColumnsCount()
            val variableStateRows = remember(columnsCount) {
                variables.values.slice(columnsCount)
            }
            val width by rememberDpState(MAX_LAYOUT_COLUMN_WIDTH)
            for (variableStateRow in variableStateRows) {
                VariablesRow(
                    modifier = Modifier.requiredWidth(width),
                    variableStateRow = variableStateRow,
                    tab = tab,
                )
            }
        }
    }

    @Composable
    private fun VariablesRow(
        modifier: Modifier = Modifier,
        variableStateRow: ImmutableList<VariableState>,
        tab: TemplateTab,
    ) {
        val errors = rememberSaveable(saver = stringToStringSnapshotStateMapSaver) {
            mutableStateMapOf()
        }
        Line(modifier) {
            for (variableState in variableStateRow) {
                VariableInput(
                    variableState = variableState,
                ) { key, error ->
                    if (error == null)
                        errors.remove(key)
                    else
                        errors[key] = error

                    val errorsCount = errors.size
                    if (errorsCount > 0) {
                        tab.badgeText.value = errorsCount.toString()
                    } else {
                        tab.badgeText.value = ""
                    }
                }
            }
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
                        CentredColumn {
                            StandardAppBar(controller, previewTab.title())
                            DefaultNavigationBar(controller, destinationsRegistry)
                        }
                    }) {
                    PaddedColumn(Modifier.contentPadding()) {
                        val dashboardWidth by rememberMaxLayoutColumnWidth()
                        ContentCard(
                            Modifier
                                .clickable(
                                    onClickLabel = stringRes(if (toolbarExpanded) R.string.collapse_template_preview_toolbar_desc else R.string.expand_template_preview_toolbar_desc),
                                ) {
                                    toolbarExpanded = toolbarExpanded.not()
                                }
                                .requiredWidth(dashboardWidth)
                        ) {
                            PaddedColumn {
                                AnimatedVisibility(toolbarExpanded) {
                                    CentredColumn {
                                        Body(previewTab.template.desc)
                                        PaddedDivider()
                                    }
                                }
                                CentredRow(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
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
                                        Body(R.string.export_pdf)
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

                        Spacer(
                            modifier = Modifier.height(
                                LocalDimens.current.content_padding.top + LocalDimens.current.content_padding.bottom
                            )
                        )

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
    icon: AbstractIcon,
    tabName: String,
    tabsBuilder: ImmutableList.Builder<AbstractDestination> = ImmutableList.builder(),
) : AbstractDestination(
    TEMPLATE_ROUTE_PREFIX + template.name + '_' + tabName,
    icon,
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

private fun tabIcon(icon: String): AbstractIcon =
    iconsAssetsResources[icon] ?: StaticIcon.baseline_table_rows

@Composable
private fun TabScaffold(
    destinationsRegistry: DestinationsRegistry,
    navController: NavHostController,
    tab: TemplateTab,
    block: @Composable () -> Unit
) {
    SimpleScaffold(
        topBar = {
            CentredColumn {
                StandardAppBar(navController, tab.title())
                DefaultNavigationBar(navController, destinationsRegistry)
            }
        },
        bottomBar = {
        }) {
        ScrollableColumn {
            ContentCard {
                PaddedColumn {
                    block()
                }
            }
        }
    }
}