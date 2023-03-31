package com.reporter.client.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.reporter.client.ui.ReporterSettingsScreen.addSettingsScreen
import com.reporter.util.ui.AbstractDestination
import com.reporter.util.ui.StandardDestinations

object ReporterStandardDestinations : StandardDestinations {

    override val settingsScreen: AbstractDestination get() = ReporterSettingsScreen

    override fun addSettingsScreen(builder: NavGraphBuilder, navController: NavController) =
        builder.addSettingsScreen(navController)
}
