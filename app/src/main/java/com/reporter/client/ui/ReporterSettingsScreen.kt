@file:OptIn(ExperimentalMaterial3Api::class)

package com.reporter.client.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.reporter.client.R
import com.reporter.util.ui.SimpleScaffold
import com.reporter.util.ui.StandardAppBar
import com.reporter.util.ui.PaddedColumn
import com.reporter.util.ui.ThemedText
import com.reporter.util.ui.AbstractDestination

object ReporterSettingsScreen : AbstractDestination(
    route = "settings",
    icon = R.drawable.baseline_settings_24,
    title = R.string.settings,
) {
    fun NavController.toSettingsScreen(navOptions: NavOptions? = null) {
        navigate(this@ReporterSettingsScreen.route, navOptions)
    }

    fun NavGraphBuilder.addSettingsScreen(navController: NavController) {
        composable(this@ReporterSettingsScreen.route) { ReporterSettingsView(navController) }
    }

    @Composable
    fun ReporterSettingsView(navController: NavController) {
        SimpleScaffold(
            topBar = {
                StandardAppBar(
                    navController = navController,
                    title = this@ReporterSettingsScreen.title,
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