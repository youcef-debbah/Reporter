@file:OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)

package com.reporter.client.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable
import com.reporter.client.R
import com.reporter.client.model.CONFIG_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED
import com.reporter.client.model.MainViewModel
import com.reporter.client.model.Template
import com.reporter.common.MIME_TYPE_APPLICATION_ZIP
import com.reporter.common.PATH_SEPARATOR
import com.reporter.common.RoundedCorner
import com.reporter.common.ioLaunch
import com.reporter.common.useInputStream
import com.reporter.util.model.Teller
import com.reporter.util.ui.AbstractApplication
import com.reporter.util.ui.AnimatedLazyLoading
import com.reporter.util.ui.ContentCard
import com.reporter.util.ui.InfoIcon
import com.reporter.util.ui.SimpleAppBar
import com.reporter.util.ui.SimpleScaffold
import com.reporter.util.ui.StaticScreenDestination
import com.reporter.util.ui.ThemedText
import com.reporter.util.ui.activeScreens
import com.reporter.util.ui.collectWithLifecycleAsState
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

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
        val resultLauncher = rememberLauncherForActivityResult(
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
                    resultLauncher.launch(newOpenTemplateFileIntent())
                }) {
                    InfoIcon(icon = R.drawable.baseline_add_24, desc = R.string.add_template_desc)
                }
            }
        ) {
            ContentCard(shape = RoundedCorner.Medium) {
                AnimatedLazyLoading(CONFIG_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED, templates) {
                    templates?.values?.forEach { item ->
                        key(item.name) {
                            TemplateCard(navController, item, viewModel)
                        }
                    }
                }
            }
        }
    }

    private fun newOpenTemplateFileIntent() = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = MIME_TYPE_APPLICATION_ZIP
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