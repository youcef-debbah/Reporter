package dz.nexatech.reporter.client.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dz.nexatech.reporter.client.ui.ReporterAboutScreen.addReporterAboutScreen
import dz.nexatech.reporter.client.ui.ReporterHelpScreen.addReporterHelpScreen
import dz.nexatech.reporter.client.ui.ReporterSettingsScreen.addSettingsScreen
import dz.nexatech.reporter.util.ui.AbstractDestination
import dz.nexatech.reporter.util.ui.StandardDestinations

object ReporterStandardDestinations : StandardDestinations {

    override val settingsScreen: AbstractDestination get() = ReporterSettingsScreen
    override val helpScreen: AbstractDestination
        get() = ReporterHelpScreen
    override val aboutScreen: AbstractDestination
        get() = ReporterAboutScreen

    override fun addSettingsScreen(
        builder: NavGraphBuilder,
        navController: NavController,
    ): ReporterSettingsScreen =
        builder.addSettingsScreen(navController)

    override fun addHelpScreen(
        builder: NavGraphBuilder,
        navController: NavController,
    ): ReporterHelpScreen =
        builder.addReporterHelpScreen(navController)

    override fun addAboutScreen(
        builder: NavGraphBuilder,
        navController: NavController,
    ): ReporterAboutScreen =
        builder.addReporterAboutScreen(navController)
}
