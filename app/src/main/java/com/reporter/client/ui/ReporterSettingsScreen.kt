@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.reporter.client.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable
import com.reporter.client.R
import com.reporter.util.ui.SimpleScaffold
import com.reporter.util.ui.StandardAppBar
import com.reporter.util.ui.PaddedColumn
import com.reporter.util.ui.ThemedText
import com.reporter.util.ui.StaticScreenDestination
import com.reporter.util.ui.activeScreens

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