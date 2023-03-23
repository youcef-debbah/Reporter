package com.reporter.client.ui

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.reporter.util.ui.AbstractDestination
import com.reporter.util.ui.StandardDestinations

object ReporterStandardDestinations : StandardDestinations {

    override val settingsScreen: AbstractDestination get() = ReporterSettingsScreen

    @Composable
    override fun SettingsScreenView(navController: NavController) =
        ReporterSettingsScreen.ReporterSettingsView(navController)
}
