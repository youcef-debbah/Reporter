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
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import dz.nexatech.reporter.client.common.atomicLazy
import dz.nexatech.reporter.client.common.backgroundLaunch
import dz.nexatech.reporter.client.common.removeIf
import dz.nexatech.reporter.client.common.slice
import dz.nexatech.reporter.client.common.withMain
import dz.nexatech.reporter.client.model.MainViewModel
import dz.nexatech.reporter.client.model.RecordState
import dz.nexatech.reporter.client.model.ResourcesRepository
import dz.nexatech.reporter.client.model.SectionState
import dz.nexatech.reporter.client.model.Template
import dz.nexatech.reporter.client.model.TemplateOutput
import dz.nexatech.reporter.client.model.TemplateState
import dz.nexatech.reporter.client.model.TemplatesRepository
import dz.nexatech.reporter.client.model.VariableState
import dz.nexatech.reporter.client.model.asWebResourceResponse
import dz.nexatech.reporter.client.model.evaluateState
import dz.nexatech.reporter.util.model.loadContent
import dz.nexatech.reporter.util.model.newDynamicWebView
import dz.nexatech.reporter.util.model.rememberColumnsCount
import dz.nexatech.reporter.util.model.rememberLayoutWidth
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


class TabsContext(
    val template: Template,
    private val templateRepository: TemplatesRepository,
) {

    companion object {
        private val context: AbstractApplication = AbstractApplication.INSTANCE
        private val resources: Resources = context.resources
        private val autoScrollAnimation: AnimationSpec<Float> = SpringSpec(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium,
        )
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
        navController: NavHostController,
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
                val templateStateJob = async { viewModel.newTemplateState(meta) }
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
        navController: NavHostController,
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
        navController: NavHostController,
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
                sectionState.badgeText,
            )
            tabsBuilder.add(tab)
            sectionTabs.add(Pair(tab, sectionState))
        }

        val recordsTabs =
            ArrayList<Pair<TemplateTab, RecordState>>(templateState.recordsStates.size)
        for (recordState in templateState.recordsStates) {
            val record = recordState.record
            val tab = TemplateTab(
                template,
                record.label,
                record.label,
                tabIcon(record.icon),
                "record_" + record.name,
                tabsBuilder,
                recordState.badgeText,
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
                request: WebResourceRequest?,
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
                    buildSectionTab(
                        this,
                        navController,
                        destinationsRegistry,
                        tab,
                        sectionState,
                    )
                }

                for (pair in recordsTabs) {
                    val tab = pair.first
                    val recordState = pair.second
                    buildRecordTab(
                        this,
                        navController,
                        destinationsRegistry,
                        tab,
                        recordState,
                        templateState,
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
        recordState: RecordState,
        templateState: TemplateState,
    ) {
        val recordDesc = recordState.record.desc
        destinationsRegistry.register(navGraphBuilder, navController) { controller ->
            composable(tab.route) {
                val scrollState = rememberScrollState()
                val coroutineScope = rememberCoroutineScope()
                TabScaffold(
                    destinationsRegistry = destinationsRegistry,
                    navController = controller,
                    tab = tab,
                    scrollState = scrollState,
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            templateState.createTuple(recordState)
                            coroutineScope.backgroundLaunch {
                                scrollState.animateScrollTo(Integer.MAX_VALUE, autoScrollAnimation)
                            }
                        }) {
                            InfoIcon(
                                icon = R.drawable.baseline_add_24,
                                desc = R.string.new_tuple_desc
                            )
                        }
                    }
                ) {
                    RecordTuples(
                        tuples = recordState.tuples,
                        desc = recordDesc,
                    ) {
                        templateState.deleteTuple(recordState, it)
                    }
                }
            }
            tab
        }
    }

    @Composable
    private fun RecordTuples(
        tuples: SnapshotStateList<ImmutableMap<String, VariableState>>,
        desc: String,
        onDelete: (ImmutableMap<String, VariableState>) -> Unit,
    ) {
        val columnsCount = rememberColumnsCount()
        val width by rememberLayoutWidth(columnsCount)

        ContentCard(
            Modifier
                .contentPadding()
                .fillMaxWidth()
        ) {
            Title(
                desc,
                Modifier
                    .contentPadding()
                    .padding(bottom = 4.dp)
                    .width(width)
            )
        }

        for (tuple in tuples) {
            val values = tuple.values
            if (values.isNotEmpty()) {
                ContentCard(Modifier.contentPadding()) {
                    PaddedColumn {
                        CentredRow {
                            Body(
                                text = stringRes(
                                    R.string.tuple_title,
                                    values.first().index
                                ),
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(onClick = { onDelete(tuple) }) {
                                InfoIcon(
                                    icon = R.drawable.baseline_delete_forever_24,
                                    desc = R.string.delete_tuple_desc
                                )
                            }
                        }
                        val variableStateRows = remember(columnsCount, tuple) {
                            values.slice(columnsCount.value)
                        }
                        for (variableStateRow in variableStateRows) {
                            Line(modifier = Modifier.requiredWidth(width)) {
                                for (variableState in variableStateRow) {
                                    VariableInput(variableState)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun buildSectionTab(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        destinationsRegistry: DestinationsRegistry,
        tab: TemplateTab,
        sectionState: SectionState,
    ) {
        val desc = sectionState.section.desc
        destinationsRegistry.register(navGraphBuilder, navController) { controller ->
            composable(tab.route) {
                TabScaffold(destinationsRegistry, controller, tab) {
                    ContentCard(Modifier.contentPadding()) {
                        PaddedColumn {
                            Title(desc, Modifier.contentPadding())
                            SectionVariables(sectionState.variables)
                        }
                    }
                }
            }
            tab
        }
    }

    @Composable
    private fun SectionVariables(
        variables: Iterable<VariableState>,
    ) {
        CentredColumn(
            Modifier.padding(
                start = Theme.dimens.content_padding.start,
                end = Theme.dimens.content_padding.end,
            )
        ) {
            val columnsCount = rememberColumnsCount()
            val width = rememberLayoutWidth(columnsCount)
            val variableStateRows = remember(columnsCount.value) {
                variables.slice(columnsCount.value)
            }
            for (variableStateRow in variableStateRows) {
                Line(modifier = Modifier.requiredWidth(width.value)) {
                    for (variableState in variableStateRow) {
                        VariableInput(variableState)
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
        webView: WebView,
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
                        val dashboardWidth by rememberLayoutWidth()
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
    badgeText: State<String> = mutableStateOf(""),
) : AbstractDestination(
    TEMPLATE_ROUTE_PREFIX + template.name + '_' + tabName,
    icon,
    badgeText
) {
    @Composable
    override fun title() = title

    @Composable
    override fun label() = label

    override val destinations: List<AbstractDestination> by atomicLazy {
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
    floatingActionButton: @Composable () -> Unit = {},
    scrollState: ScrollState = rememberScrollState(),
    block: @Composable () -> Unit,
) {
    SimpleScaffold(
        topBar = {
            CentredColumn {
                StandardAppBar(navController, tab.title())
                DefaultNavigationBar(navController, destinationsRegistry)
            }
        },
        floatingActionButton = floatingActionButton
    ) {
        ScrollableColumn(scrollState = scrollState) {
            block()
        }
    }
}