@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

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
import dz.nexatech.reporter.util.ui.ThemedText
import dz.nexatech.reporter.util.ui.StaticScreenDestination
import dz.nexatech.reporter.util.ui.activeScreens

object ReporterSettingsScreen : StaticScreenDestination(
    route = "settings",
    icon = R.drawable.baseline_settings_24,
    titleRes = R.string.settings,
) {
    fun NavController.toSettingsScreen(navOptions: NavOptions? = null) {
        navigate(this@ReporterSettingsScreen.route, navOptions)
    }

    fun NavGraphBuilder.addSettingsScreen(navController: NavController) {
        val thisRoute = this@ReporterSettingsScreen.route
        activeScreens[thisRoute] = this@ReporterSettingsScreen
        composable(thisRoute) { ReporterSettingsView(navController) }
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
                ThemedText("Settings Page")//TODO
            }
        }
    }
}