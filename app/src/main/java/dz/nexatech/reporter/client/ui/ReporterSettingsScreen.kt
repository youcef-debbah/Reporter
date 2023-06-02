@file:OptIn(ExperimentalAnimationApi::class)

package dz.nexatech.reporter.client.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.util.ui.SimpleScaffold
import dz.nexatech.reporter.util.ui.StandardAppBar
import dz.nexatech.reporter.util.ui.PaddedColumn
import dz.nexatech.reporter.util.ui.Body
import dz.nexatech.reporter.util.ui.StaticIcon
import dz.nexatech.reporter.util.ui.StaticScreenDestination

object ReporterSettingsScreen : StaticScreenDestination(
    route = "settings",
    icon = StaticIcon.baseline_settings,
    titleRes = R.string.settings,
) {
    fun NavController.toSettingsScreen(navOptions: NavOptions? = null) {
        navigate(this@ReporterSettingsScreen.route, navOptions)
    }

    fun NavGraphBuilder.addSettingsScreen(navController: NavController): ReporterSettingsScreen {
        val thisRoute = this@ReporterSettingsScreen.route
        composable(thisRoute) { ReporterSettingsView(navController) }
        return this@ReporterSettingsScreen
    }

    @Composable
    fun ReporterSettingsView(navController: NavController) {
        SimpleScaffold(
            topBar = {
                StandardAppBar(
                    navController = navController,
                    title = this@ReporterSettingsScreen.titleRes,
                    actions = {},
                )
            },
        ) {
            PaddedColumn {
                Body("Settings Page")//TODO
            }
        }
    }
}