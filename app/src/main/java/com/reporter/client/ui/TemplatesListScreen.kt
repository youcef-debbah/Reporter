@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.reporter.client.ui

import android.content.res.Resources
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.createGraph
import com.google.accompanist.navigation.animation.composable
import com.google.common.collect.ImmutableList
import com.reporter.client.R
import com.reporter.client.model.Record
import com.reporter.client.model.Template
import com.reporter.client.model.TemplateMeta
import com.reporter.common.RoundedCorner
import com.reporter.common.Texts
import com.reporter.common.backgroundLaunch
import com.reporter.common.mainLaunch
import com.reporter.common.removeIf
import com.reporter.util.ui.AbstractApplication
import com.reporter.util.ui.AbstractDestination
import com.reporter.util.ui.ContentCard
import com.reporter.util.ui.DecorativeIcon
import com.reporter.util.ui.DefaultNavigationBar
import com.reporter.util.ui.ErrorTheme
import com.reporter.util.ui.InfoIcon
import com.reporter.util.ui.SimpleAppBar
import com.reporter.util.ui.SimpleScaffold
import com.reporter.util.ui.StaticScreenDestination
import com.reporter.util.ui.ThemedText
import com.reporter.util.ui.activeScreens
import com.reporter.util.ui.contentPadding
import kotlinx.coroutines.delay

object TemplatesListScreen : StaticScreenDestination(
    route = "templates_list",
    icon = R.drawable.baseline_home_24,
    titleRes = R.string.templates_list_title,
    labelRes = R.string.templates_list_label,
) {

    fun NavController.toTemplatesListScreen(navOptions: NavOptions? = null) {
        navigate(this@TemplatesListScreen.route, navOptions)
    }

    fun NavGraphBuilder.addTemplatesListScreen(navController: NavHostController, webView: WebView) {
        val thisRoute = this@TemplatesListScreen.route
        activeScreens[thisRoute] = this@TemplatesListScreen

        val templates = listOf(
            Template(
                "temp_1",
                "${Texts.ASSETS_URL_PREFIX}wood_bill.html",
                "Wood bill",
                "فاتورة الحطب",
                "Facture de bois",
                "Standard wood bill for small clients",
                "فاتورة خشب قياسية للعملاء الصغار",
                "Facture de bois standard pour les petits clients",
                System.currentTimeMillis(),
            ),
            Template(
                "temp_2",
                "<p>This is my Water bill</p>",
                "Water bill",
                "فاتورة ماء",
                "Facture de l'eau",
                "Standard water bill for small clients",
                "فاتورة ماء قياسية للعملاء الصغار",
                "Facture de l'eau standard pour les petits clients",
                System.currentTimeMillis(),
            )
        )

        composable(thisRoute) { TemplatesListView(navController, templates, webView) }
    }

    @Composable
    private fun TemplatesListView(
        navController: NavHostController,
        templates: List<Template>,
        webView: WebView
    ) {
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
                for (template in templates) {
                    key(template.name) {
                        TemplateCard(navController, webView, template)
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

        val loadingRoute = buildAndNavigateToLoadingView(navController, template, resources)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                view?.evaluateJavascript("meta") {
                    backgroundLaunch {
                        val meta = TemplateMeta.from(it)
                        mainLaunch {
                            if (meta.hasErrors()) {
                                buildAndNavigateToErrorView(navController, template, loadingRoute, resources)
                            } else {
                                buildAndNavigateToTemplateView(
                                    navController,
                                    webView,
                                    template,
                                    meta,
                                    loadingRoute,
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
): String {
    val newScreen = TemplateTab(
        template,
        resources.getString(R.string.template_tab_loading_title, template.label),
        resources.getString(R.string.template_tab_loading_label),
        R.drawable.baseline_downloading_24,
        "loading",
    )

    val newGraph =
        navController.createGraph(newScreen.route, TemplateTab.GLOBAL_ROUTE_PREFIX + "_loading") {
            activeScreens[newScreen.route] = newScreen
            composable(newScreen.route) {
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
    return newScreen.route
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
    navController.navigate(newScreen.route)  {
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
                        ThemedText(previewTab.template.desc)
                        AndroidView(factory = { webView }, modifier = Modifier.fillMaxSize())
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
                            ThemedText("record name:" + tab.record.name)
                            ThemedText("record label: " + tab.record.label)
                            ThemedText("record desc: " + tab.record.desc)
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