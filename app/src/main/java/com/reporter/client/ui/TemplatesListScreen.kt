@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.reporter.client.ui

import android.content.res.Resources
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.createGraph
import com.google.accompanist.navigation.animation.composable
import com.google.common.collect.ImmutableList
import com.reporter.client.R
import com.reporter.client.model.MainViewModel
import com.reporter.client.model.Record
import com.reporter.client.model.Template
import com.reporter.client.model.TemplateMeta
import com.reporter.common.AsyncConfig
import com.reporter.common.RoundedCorner
import com.reporter.common.backgroundScope
import com.reporter.common.ioScope
import com.reporter.common.mainScope
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
import com.reporter.util.ui.SimpleAppBar
import com.reporter.util.ui.SimpleScaffold
import com.reporter.util.ui.StaticScreenDestination
import com.reporter.util.ui.ThemedText
import com.reporter.util.ui.activeScreens
import com.reporter.util.ui.collectWithLifecycleAsState
import com.reporter.util.ui.contentPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

object TemplatesListScreen : StaticScreenDestination(
    route = "templates_list",
    icon = R.drawable.baseline_home_24,
    titleRes = R.string.templates_list_title,
    labelRes = R.string.templates_list_label,
) {

    fun NavController.toTemplatesListScreen(navOptions: NavOptions? = null) {
        navigate(this@TemplatesListScreen.route, navOptions)
    }

    fun NavGraphBuilder.addTemplatesListScreen(
        navController: NavHostController,
        webView: WebView,
        viewModel: MainViewModel,
    ) {
        val thisRoute = this@TemplatesListScreen.route
        activeScreens[thisRoute] = this@TemplatesListScreen
        composable(thisRoute) { TemplatesListView(navController, webView, viewModel) }
    }

    @Composable
    private fun TemplatesListView(
        navController: NavHostController,
        webView: WebView,
        viewModel: MainViewModel,
    ) {
        val templateState = viewModel.templatesRepository.templates.collectWithLifecycleAsState()
        val templates = templateState.value
        SimpleScaffold(
            topBar = {
                SimpleAppBar(
                    this@TemplatesListScreen.titleRes,
                    this@TemplatesListScreen.icon
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    // TODO
                }) {
                    InfoIcon(icon = R.drawable.baseline_add_24, desc = R.string.add_template_desc)
                }
            }
        ) {
            ContentCard(shape = RoundedCorner.Medium) {
                AnimatedVisibility(templates == null) {
                    PaddedColumn(Modifier.contentPadding()) {
                        ThemedText(R.string.loading_templates_list_desc)
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
                AnimatedVisibility(templates != null) {
                    PaddedColumn {
                        templates?.forEach { item ->
                            key(item.name) {
                                TemplateCard(navController, webView, item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateCard(navController: NavHostController, webView: WebView, template: Template) {
    Card(onClick = {
        val resources = AbstractApplication.INSTANCE.resources

        // remove old screens
        navController.graph.iterator().removeIf(postRemove = { activeScreens.remove(it.route) }) {
            it.route?.startsWith(TemplateTab.GLOBAL_ROUTE_PREFIX) ?: false
        }

        val loadingScope = buildAndNavigateToLoadingView(navController, template, resources)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                view?.evaluateJavascript("meta") {
                    loadingScope.background().launch {
                        val meta = TemplateMeta.from(it)
                        withMain {
                            if (meta.hasErrors()) {
                                buildAndNavigateToErrorView(
                                    navController,
                                    template,
                                    loadingScope.route,
                                    resources
                                )
                            } else {
                                buildAndNavigateToTemplateView(
                                    navController,
                                    webView,
                                    template,
                                    meta,
                                    loadingScope.route,
                                    resources,
                                )
                            }
                        }
                    }
                }
            }
        }

        template.loadContent(webView)
    }) {
        ThemedText(template.label)
    }
}

private fun buildAndNavigateToLoadingView(
    navController: NavHostController,
    template: Template,
    resources: Resources,
): ScreenScope {
    val newScreen = TemplateTab(
        template,
        resources.getString(R.string.template_tab_loading_title, template.label),
        resources.getString(R.string.template_tab_loading_label),
        R.drawable.baseline_downloading_24,
        "loading",
    )

    val result = ScreenScope(newScreen.route)
    val newGraph =
        navController.createGraph(newScreen.route, TemplateTab.GLOBAL_ROUTE_PREFIX + "_loading") {
            activeScreens[newScreen.route] = newScreen
            composable(newScreen.route) {
                result.scope = rememberCoroutineScope()
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
    navController.navigate(newScreen.route)
    return result
}

class ScreenScope(
    val route: String,
    @Volatile var scope: CoroutineScope? = null,
) {
    fun main(): CoroutineScope = scope?.plus(AsyncConfig.mainDispatcher) ?: mainScope()

    fun background(): CoroutineScope =
        scope?.plus(AsyncConfig.backgroundDispatcher) ?: backgroundScope()

    fun io(): CoroutineScope = scope?.plus(AsyncConfig.ioDispatcher) ?: ioScope()

    override fun toString() = "ScreenScope(route='$route')"
}

private fun buildAndNavigateToErrorView(
    navController: NavHostController,
    template: Template,
    loadingRoute: String,
    resources: Resources,
) {
    val newScreen = TemplateTab(
        template,
        resources.getString(R.string.template_tab_error_title),
        null,
        R.drawable.baseline_error_24,
        "error",
    )

    val newGraph =
        navController.createGraph(newScreen.route, TemplateTab.GLOBAL_ROUTE_PREFIX + "_error") {
            activeScreens[newScreen.route] = newScreen
            composable(newScreen.route) {
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
                            }
                        }
                    }
                }
            }
        }

    navController.graph.addAll(newGraph)
    navController.navigate(newScreen.route) {
        popUpTo(loadingRoute) {
            inclusive = true
        }
    }
}

private fun buildAndNavigateToTemplateView(
    navController: NavHostController,
    webView: WebView,
    template: Template,
    meta: TemplateMeta,
    loadingRoute: String,
    resources: Resources,
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

    val recordTabs = ArrayList<RecordTab>(meta.records.size)
    for (record in meta.records.values) {
        val recordTab = RecordTab(
            record,
            template,
            record.label,
            record.label,
            R.drawable.baseline_table_rows_24,
            "record_" + record.name,
            tabsBuilder,
        )
        tabsBuilder.add(recordTab)
        recordTabs.add(recordTab)
    }

    val newGraph =
        navController.createGraph(previewTab.route, TemplateTab.GLOBAL_ROUTE_PREFIX + "_loaded") {
            activeScreens[previewTab.route] = previewTab
            composable(previewTab.route) {
                SimpleScaffold(
                    topBar = {
                        SimpleAppBar(previewTab.title())
                    },
                    bottomBar = {
                        DefaultNavigationBar(navController)
                    }) {
                    ContentCard {
                        PaddedColumn {
                            ThemedText(previewTab.template.desc)
                            AndroidView(
                                factory = { webView },
                                modifier = Modifier
                                    .contentPadding()
                                    .fillMaxSize()
                            )
                        }
                    }
                }
            }

            for (tab in recordTabs) {
                activeScreens[tab.route] = tab
                composable(tab.route) {
                    SimpleScaffold(
                        topBar = {
                            SimpleAppBar(tab.title())
                        },
                        bottomBar = {
                            DefaultNavigationBar(navController)
                        }) {
                        ContentCard {
                            PaddedColumn {
                                TextField(value = "", onValueChange = {})
                                ThemedText("Record name:" + tab.record.name)
                                ThemedText("Record label: " + tab.record.label)
                                ThemedText("Record desc: " + tab.record.desc)
                            }
                        }
                    }
                }
            }
        }

    navController.graph.addAll(newGraph)
    navController.navigate(previewTab.route) {
        popUpTo(loadingRoute) {
            inclusive = true
        }
    }
}

@Immutable
private open class TemplateTab(
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

@Immutable
private class RecordTab(
    val record: Record,
    template: Template,
    title: String,
    label: String?,
    @DrawableRes tabIcon: Int,
    tabName: String,
    tabsBuilder: ImmutableList.Builder<AbstractDestination> = ImmutableList.builder(),
) : TemplateTab(template, title, label, tabIcon, tabName, tabsBuilder)