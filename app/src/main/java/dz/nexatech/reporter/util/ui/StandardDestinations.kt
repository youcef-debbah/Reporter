package dz.nexatech.reporter.util.ui

import androidx.compose.runtime.Immutable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dz.nexatech.reporter.client.ui.ReporterAboutScreen
import dz.nexatech.reporter.client.ui.ReporterHelpScreen
import dz.nexatech.reporter.client.ui.ReporterSettingsScreen

@Immutable
interface StandardDestinations {

    val settingsScreen: AbstractDestination
    val helpScreen: AbstractDestination
    val aboutScreen: AbstractDestination

    fun addSettingsScreen(
        builder: NavGraphBuilder,
        navController: NavController,
    ): ReporterSettingsScreen

    fun addHelpScreen(builder: NavGraphBuilder, navController: NavController): ReporterHelpScreen

    fun addAboutScreen(builder: NavGraphBuilder, navController: NavController): ReporterAboutScreen
}