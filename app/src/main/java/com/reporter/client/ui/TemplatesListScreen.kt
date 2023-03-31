@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.reporter.client.ui

import android.webkit.WebView
import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.createGraph
import com.google.accompanist.navigation.animation.composable
import com.google.common.collect.ImmutableList
import com.reporter.client.R
import com.reporter.client.model.ReportTemplate
import com.reporter.common.RoundedCorner
import com.reporter.common.removeIf
import com.reporter.util.ui.AbstractDestination
import com.reporter.util.ui.ContentCard
import com.reporter.util.ui.DefaultNavigationBar
import com.reporter.util.ui.InfoIcon
import com.reporter.util.ui.SimpleAppBar
import com.reporter.util.ui.SimpleScaffold
import com.reporter.util.ui.StaticScreenDestination
import com.reporter.util.ui.ThemedText
import com.reporter.util.ui.activeScreens

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
            ReportTemplate(
                1,
                "temp_1",
                "<p>This is my Wood bill</p>",
                "Wood bill",
                "فاتورة الحطب",
                "Facture de bois",
                "Standard wood bill for small clients",
                "فاتورة خشب قياسية للعملاء الصغار",
                "Facture de bois standard pour les petits clients"
            ),
            ReportTemplate(
                2,
                "temp_2",
                "<p>This is my Water bill</p>",
                "Water bill",
                "فاتورة ماء",
                "Facture de l'eau",
                "Standard water bill for small clients",
                "فاتورة ماء قياسية للعملاء الصغار",
                "Facture de l'eau standard pour les petits clients"
            )
        )

        composable(thisRoute) { TemplatesListView(navController, templates, webView) }
    }

    @Composable
    private fun TemplatesListView(
        navController: NavHostController,
        templates: List<ReportTemplate>,
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
                        TemplateCard(navController, template, webView)
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateCard(navController: NavHostController, template: ReportTemplate, webView: WebView) {
    Card(onClick = {
        val graph = navController.graph
        graph.iterator().removeIf(postRemove = { activeScreens.remove(it.route) }) {
            it.route?.startsWith(TemplateTab.GLOBAL_ROUTE_PREFIX) ?: false
        }

        val tabsBuilder: ImmutableList.Builder<AbstractDestination> = ImmutableList.builder()
        val previewTab = TemplateTab(template, tabsBuilder, "preview", R.drawable.baseline_preview_24)

        val tabsGraph =
            navController.createGraph(previewTab.route, TemplateTab.GLOBAL_ROUTE_PREFIX) {
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
                            ThemedText(previewTab.template.desc) //TODO
                        }
                    }
                }
            }

        graph.addAll(tabsGraph)
        navController.navigate(tabsBuilder.build().first().route)
    }) {
        ThemedText(template.label)
    }
}

@Immutable
private class TemplateTab(
    val template: ReportTemplate,
    tabsBuilder: ImmutableList.Builder<AbstractDestination>,
    tabName: String,
    @DrawableRes tabIcon: Int,
) : AbstractDestination(
    GLOBAL_ROUTE_PREFIX + template.name + tabName,
    tabIcon,
) {

    init {
        tabsBuilder.add(this)
    }

    @Composable
    @ReadOnlyComposable
    override fun title(): String =
        LocalContext.current.getString(R.string.template_tab_preview_title, template.label)

    @Composable
    @ReadOnlyComposable
    override fun label(): String = LocalContext.current.getString(R.string.preview)

    override val destinations: List<AbstractDestination> by lazy { tabsBuilder.build() }

    companion object {
        const val GLOBAL_ROUTE_PREFIX = "template_"
    }
}
