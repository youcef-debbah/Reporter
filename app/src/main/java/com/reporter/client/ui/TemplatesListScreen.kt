@file:OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)

package com.reporter.client.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable
import com.reporter.client.R
import com.reporter.client.model.CONFIG_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED
import com.reporter.client.model.MainViewModel
import com.reporter.client.model.Template
import com.reporter.common.MIME_TYPE_ANY
import com.reporter.common.MIME_TYPE_APPLICATION_ZIP
import com.reporter.common.RoundedCorner
import com.reporter.util.model.AppConfig
import com.reporter.util.model.REMOTE_TEMPLATES_DOWNLOADING_LINK
import com.reporter.util.ui.AnimatedLazyLoading
import com.reporter.util.ui.ContentCard
import com.reporter.util.ui.DecorativeIcon
import com.reporter.util.ui.ExternalLink
import com.reporter.util.ui.InfoIcon
import com.reporter.util.ui.SimpleAppBar
import com.reporter.util.ui.SimpleScaffold
import com.reporter.util.ui.StaticScreenDestination
import com.reporter.util.ui.ThemedText
import com.reporter.util.ui.activeScreens
import com.reporter.util.ui.collectWithLifecycleAsState
import com.reporter.util.ui.contentPadding

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
        viewModel: MainViewModel,
    ) {
        val thisRoute = this@TemplatesListScreen.route
        activeScreens[thisRoute] = this@TemplatesListScreen
        composable(thisRoute) { TemplatesListView(navController, viewModel) }
    }

    @Composable
    private fun TemplatesListView(
        navController: NavHostController,
        viewModel: MainViewModel,
    ) {
        val templateState = viewModel.templates().collectWithLifecycleAsState()
        val templates = templateState.value
        val templateImportLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            result.data?.data?.let { uri ->
                viewModel.importTemplate(uri)
            }
        }
        SimpleScaffold(
            topBar = {
                SimpleAppBar(
                    this@TemplatesListScreen.titleRes,
                    this@TemplatesListScreen.icon
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    templateImportLauncher.launch(newOpenTemplateFileIntent())
                }) {
                    InfoIcon(
                        icon = R.drawable.baseline_upload_file_24,
                        desc = R.string.add_template_desc
                    )
                }
            }
        ) {
            ContentCard(shape = RoundedCorner.Medium) {
                AnimatedLazyLoading(CONFIG_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED, templates) {
                    val items = templates?.values
                    if (items != null) {
                        if (items.isEmpty()) {
                            ThemedText(textRes = R.string.no_templates_found)
                            Row {
                                Button(
                                    modifier = Modifier.contentPadding(),
                                    onClick = {
                                        templateImportLauncher.launch(
                                            newOpenTemplateFileIntent()
                                        )
                                    }) {
                                    DecorativeIcon(icon = R.drawable.baseline_upload_file_24)
                                    ThemedText(textRes = R.string.import_template)
                                }
                                Button(modifier = Modifier.contentPadding(),
                                    onClick = {
                                        ExternalLink.openLink(
                                            AppConfig.get(
                                                REMOTE_TEMPLATES_DOWNLOADING_LINK
                                            )
                                        )
                                    }) {
                                    DecorativeIcon(icon = R.drawable.baseline_open_in_browser_24)
                                    ThemedText(textRes = R.string.download_templates)
                                }
                            }
                        } else {
                            items.forEach { item ->
                                key(item.name) {
                                    TemplateCard(navController, item, viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun newOpenTemplateFileIntent() = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = MIME_TYPE_ANY
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
    }
}

@Composable
fun TemplateCard(
    navController: NavHostController,
    template: Template,
    viewModel: MainViewModel,
) {
    Card(onClick = {
        viewModel.navigateToTemplateTabs(template, navController)
    }) {
        ThemedText(template.label)
    }
}