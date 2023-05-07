@file:OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class
)

package dz.nexatech.reporter.client.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.MimeType
import dz.nexatech.reporter.client.model.MainViewModel
import dz.nexatech.reporter.client.model.REMOTE_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED
import dz.nexatech.reporter.client.model.Template
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.REMOTE_TEMPLATES_DOWNLOADING_LINK
import dz.nexatech.reporter.util.ui.AnimatedLazyLoading
import dz.nexatech.reporter.util.ui.CentredColumn
import dz.nexatech.reporter.util.ui.ContentCard
import dz.nexatech.reporter.util.ui.DecorativeIcon
import dz.nexatech.reporter.util.ui.ExternalLink
import dz.nexatech.reporter.util.ui.RoundedCorner
import dz.nexatech.reporter.util.ui.SimpleScaffold
import dz.nexatech.reporter.util.ui.StandardAppBar
import dz.nexatech.reporter.util.ui.StandardAppBarDropdownMenu
import dz.nexatech.reporter.util.ui.StaticScreenDestination
import dz.nexatech.reporter.util.ui.ThemedText
import dz.nexatech.reporter.util.ui.collectWithLifecycleAsState
import dz.nexatech.reporter.util.ui.contentPadding
import dz.nexatech.reporter.util.ui.drawVerticalScrollbar

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
    ): TemplatesListScreen {
        val thisRoute = this@TemplatesListScreen.route
        composable(thisRoute) { TemplatesListView(navController, viewModel) }
        return this@TemplatesListScreen
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
                StandardAppBar(
                    navController = navController,
                    title = this@TemplatesListScreen.titleRes,
                    navigationIcon = { DecorativeIcon(icon = this@TemplatesListScreen.icon) },
                ) {
                    StandardAppBarDropdownMenu(navController) { menuOpened ->
                        DropdownMenuItem(
                            enabled = viewModel.templateImporting.value == 0,
                            leadingIcon = { DecorativeIcon(icon = R.drawable.baseline_upload_file_24) },
                            text = { ThemedText(R.string.import_template_menu_item) },
                            onClick = {
                                menuOpened.value = false
                                templateImportLauncher.launch(newOpenTemplateFileIntent())
                            }
                        )

                        DropdownMenuItem(
                            leadingIcon = { DecorativeIcon(icon = R.drawable.baseline_open_in_browser_24) },
                            text = { ThemedText(R.string.download_template_menu_item) },
                            onClick = {
                                menuOpened.value = false
                                ExternalLink.openLink(
                                    AppConfig.get(REMOTE_TEMPLATES_DOWNLOADING_LINK)
                                )
                            }
                        )
                    }
                }
            },
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .drawVerticalScrollbar(scrollState)
                    .verticalScroll(
                        state = scrollState,
                        enabled = true,
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ContentCard(shape = RoundedCorner.Medium) {
                    if (viewModel.templateImporting.value > 0) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                    }
                    AnimatedLazyLoading(
                        REMOTE_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED,
                        templates
                    ) {
                        val items = templates?.values
                        if (items != null) {
                            if (items.isEmpty()) {
                                ThemedText(textRes = R.string.no_templates_found)
                                FlowRow(horizontalArrangement = Arrangement.SpaceEvenly) {
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
                                                AppConfig.get(REMOTE_TEMPLATES_DOWNLOADING_LINK)
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
    }

    private fun newOpenTemplateFileIntent() = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = MimeType.ANY
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
    }
}

@Composable
fun TemplateCard(
    navController: NavHostController,
    template: Template,
    viewModel: MainViewModel,
) {
    Card(
        Modifier
            .contentPadding()
            .fillMaxWidth()
    ) {
        CentredColumn(Modifier.clickable {
            viewModel.navigateToTemplateTabs(template, navController)
        }) {
            ThemedText(text = template.label, style = MaterialTheme.typography.titleLarge)
            ThemedText(text = template.desc, style = MaterialTheme.typography.bodyLarge)
        }
    }
}