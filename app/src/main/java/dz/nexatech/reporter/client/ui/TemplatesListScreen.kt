@file:OptIn(ExperimentalMaterial3Api::class)

package dz.nexatech.reporter.client.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
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
import dz.nexatech.reporter.util.ui.ContentCard
import dz.nexatech.reporter.util.ui.DropdownMenuTextItem
import dz.nexatech.reporter.util.ui.ExternalLink
import dz.nexatech.reporter.util.ui.PaddedColumn
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
import dz.nexatech.reporter.util.ui.SurroundedLink
import dz.nexatech.reporter.util.ui.Theme
import dz.nexatech.reporter.util.ui.Title
import dz.nexatech.reporter.util.ui.contentPadding
import dz.nexatech.reporter.util.ui.navigate
import dz.nexatech.reporter.util.ui.rememberTextWithLink
import dz.nexatech.reporter.util.ui.small_padding
import dz.nexatech.reporter.util.ui.themedComposable
import kotlinx.serialization.Serializable

@Serializable
object TemplatesListScreen : StaticScreenDestination(
    screenRoute = "templates_list",
    screenIcon = StaticIcon.baseline_home,
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
        themedComposable(thisRoute) { TemplatesListView(navController, viewModel) }
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
                    currentRoute = this@TemplatesListScreen.route,
                    title = this@TemplatesListScreen.titleRes,
                    navigationIcon = { StandardAppbarIcon(this@TemplatesListScreen.icon) },
                ) {
                    StandardAppBarDropdownMenu(
                        this@TemplatesListScreen.route,
                        navController
                    ) { menuOpened ->
                        DropdownMenuTextItem(
                            title = R.string.import_template_menu_item,
                            icon = StaticIcon.baseline_upload_file,
                            enabled = viewModel.templateImporting.intValue == 0,
                        ) {
                            menuOpened.value = false
                            templateImportLauncher.launch(newOpenTemplateFileIntent())
                        }

                        DropdownMenuTextItem(
                            title = R.string.download_template_menu_item,
                            icon = StaticIcon.baseline_cloud_download,
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
                        if (viewModel.templateImporting.intValue > 0) {
                            LinearProgressIndicator(Modifier.fillMaxWidth())
                        }
                        AnimatedLazyLoading(
                            modifier = Modifier.fillMaxWidth(),
                            animationEnabled = TEMPLATES_LIST_LOADING_ANIMATION_ENABLED,
                            data = templates
                        ) {
                            val items = templates?.values
                            if (items.isNullOrEmpty()) {
                                PaddedColumn(
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier.contentPadding()
                                ) {
                                    Body(
                                        textRes = R.string.empty_templates_hint,
                                        modifier = Modifier.padding(bottom = small_padding * 4),
                                        fontSize = Theme.typography.bodyLarge.fontSize,
                                    )
                                    val downloadHint = rememberTextWithLink(
                                        prefix = R.string.download_hint_prefix,
                                        label = R.string.download_hint_label,
                                        suffix = R.string.download_hint_suffix
                                    )
                                    SurroundedLink(downloadHint) {
                                        ExternalLink.openLink(
                                            AppConfig.get(TEMPLATES_DOWNLOADING_LINK)
                                        )
                                    }
                                    val importHint = rememberTextWithLink(
                                        prefix = R.string.import_hint_prefix,
                                        label = R.string.import_hint_label,
                                        suffix = R.string.import_hint_suffix
                                    )
                                    SurroundedLink(importHint) {
                                        templateImportLauncher.launch(
                                            newOpenTemplateFileIntent()
                                        )
                                    }
                                    HelpHint(navController)
                                    BuyHint(navController)
                                }
                            } else {
                                items.forEach { item ->
                                    key(item.name) {
                                        TemplateCard(navController, item, viewModel)
                                    }
                                }
                                PaddedDivider()
                                PaddedColumn(
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier.contentPadding().fillMaxWidth()
                                ) {
                                    HelpHint(navController)
                                    BuyHint(navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun BuyHint(navController: NavHostController) {
        val buyHint = rememberTextWithLink(
            prefix = R.string.buy_hint_prefix,
            label = R.string.buy_hint_label,
            suffix = R.string.buy_hint_suffix
        )
        SurroundedLink(buyHint) {
            navController.navigate(ReporterHelpScreen)
        }
    }

    @Composable
    private fun HelpHint(navController: NavHostController) {
        val downloadHint = rememberTextWithLink(
            prefix = R.string.more_help_hint_prefix,
            label = R.string.reporter_help_title,
            suffix = R.string.more_help_hint_suffix
        )
        SurroundedLink(downloadHint) {
            navController.navigate(ReporterHelpScreen)
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