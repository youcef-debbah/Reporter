@file:OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class,
)

package dz.nexatech.reporter.client.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.MimeType
import dz.nexatech.reporter.client.model.MainViewModel
import dz.nexatech.reporter.client.model.TEMPLATES_LIST_LOADING_ANIMATION_ENABLED
import dz.nexatech.reporter.client.model.Template
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.TEMPLATES_DOWNLOADING_LINK
import dz.nexatech.reporter.util.model.rememberLayoutWidth
import dz.nexatech.reporter.util.ui.AnimatedLazyLoading
import dz.nexatech.reporter.util.ui.Body
import dz.nexatech.reporter.util.ui.CentredColumn
import dz.nexatech.reporter.util.ui.CentredRow
import dz.nexatech.reporter.util.ui.ContentCard
import dz.nexatech.reporter.util.ui.DecorativeIcon
import dz.nexatech.reporter.util.ui.DropdownMenuTextItem
import dz.nexatech.reporter.util.ui.ExternalLink
import dz.nexatech.reporter.util.ui.PaddedDivider
import dz.nexatech.reporter.util.ui.PaddedRow
import dz.nexatech.reporter.util.ui.RoundedCorner
import dz.nexatech.reporter.util.ui.ScrollableColumn
import dz.nexatech.reporter.util.ui.SimpleScaffold
import dz.nexatech.reporter.util.ui.StandardAppBar
import dz.nexatech.reporter.util.ui.StandardAppBarDropdownMenu
import dz.nexatech.reporter.util.ui.StandardAppbarIcon
import dz.nexatech.reporter.util.ui.StaticIcon
import dz.nexatech.reporter.util.ui.StaticScreenDestination
import dz.nexatech.reporter.util.ui.Title
import dz.nexatech.reporter.util.ui.contentPadding

object TemplatesListScreen : StaticScreenDestination(
    route = "templates_list",
    icon = StaticIcon.baseline_home,
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
        val templates = viewModel.templates.value
        val templateImportLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            result.data?.data?.let { uri ->
                viewModel.importTemplate(uri, navController)
            }
        }
        val showDeleteAllTemplatesDialog = rememberSaveable { mutableStateOf(false) }
        SimpleScaffold(
            topBar = {
                StandardAppBar(
                    navController = navController,
                    title = this@TemplatesListScreen.titleRes,
                    navigationIcon = { StandardAppbarIcon(this@TemplatesListScreen.icon) },
                ) {
                    StandardAppBarDropdownMenu(navController) { menuOpened ->
                        DropdownMenuTextItem(
                            title = R.string.import_template_menu_item,
                            icon = StaticIcon.baseline_upload_file,
                            enabled = viewModel.templateImporting.value == 0,
                        ) {
                            menuOpened.value = false
                            templateImportLauncher.launch(newOpenTemplateFileIntent())
                        }

                        DropdownMenuTextItem(
                            title = R.string.download_template_menu_item,
                            icon = StaticIcon.baseline_open_in_browser,
                        ) {
                            menuOpened.value = false
                            ExternalLink.openLink(
                                AppConfig.get(TEMPLATES_DOWNLOADING_LINK)
                            )
                        }

                        DropdownMenuTextItem(
                            title = R.string.delete_templates_menu_item,
                            icon = StaticIcon.baseline_delete_forever,
                        ) {
                            menuOpened.value = false
                            showDeleteAllTemplatesDialog.value = true
                        }
                    }
                }
            },
        ) {

            val closeDialog = { showDeleteAllTemplatesDialog.value = false }
            if (showDeleteAllTemplatesDialog.value) {
                AlertDialog(onDismissRequest = closeDialog) {
                    ContentCard {
                        CentredColumn {
                            Title(
                                R.string.dialog_title_delete_confirmation,
                                Modifier.contentPadding(),
                            )
                            PaddedDivider()
                            Body(R.string.confirm_deleting_all_templates)
                            PaddedRow(
                                modifier = Modifier
                                    .contentPadding()
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                Button(onClick = {
                                    viewModel.deleteAllTemplates()
                                    closeDialog()
                                }) {
                                    Body(R.string.yes)
                                }
                                Button(
                                    onClick = closeDialog,
                                ) {
                                    Body(R.string.no)
                                }
                            }
                        }
                    }
                }
            }

            ScrollableColumn(Modifier.contentPadding()) {
                val templatesListWidth by rememberLayoutWidth()
                ContentCard(shape = RoundedCorner.Medium) {
                    CentredColumn(modifier = Modifier.requiredWidth(templatesListWidth)) {
                        if (viewModel.templateImporting.value > 0) {
                            LinearProgressIndicator(Modifier.fillMaxWidth())
                        }
                        AnimatedLazyLoading(
                            modifier = Modifier.requiredWidth(templatesListWidth),
                            animationEnabled = TEMPLATES_LIST_LOADING_ANIMATION_ENABLED,
                            data = templates
                        ) {
                            val items = templates?.values
                            if (items != null) {
                                if (items.isEmpty()) {
                                    Title(R.string.no_templates_found, Modifier.contentPadding())
                                    PaddedDivider()
                                    CentredRow(Modifier.fillMaxWidth()) {
                                        Button(
                                            modifier = Modifier.contentPadding(),
                                            onClick = {
                                                templateImportLauncher.launch(
                                                    newOpenTemplateFileIntent()
                                                )
                                            }) {
                                            DecorativeIcon(icon = R.drawable.baseline_upload_file_24)
                                            Body(textRes = R.string.import_template)
                                        }
                                        Button(modifier = Modifier.contentPadding(),
                                            onClick = {
                                                ExternalLink.openLink(
                                                    AppConfig.get(TEMPLATES_DOWNLOADING_LINK)
                                                )
                                            }) {
                                            DecorativeIcon(icon = R.drawable.baseline_open_in_browser_24)
                                            Body(textRes = R.string.download_templates)
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
    }

    private fun newOpenTemplateFileIntent() = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = MimeType.ANY
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
    }
}

@Composable
fun TemplateCard(
    navController: NavHostController,
    template: Template,
    viewModel: MainViewModel,
) {
    Card(
        Modifier.contentPadding()
    ) {
        CentredColumn(
            Modifier
                .clickable {
                    viewModel.navigateToTemplateTabs(template, navController)
                }
                .fillMaxWidth()
        ) {
            Title(text = template.label)
            Body(text = template.desc)
        }
    }
}