package dz.nexatech.reporter.client.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dz.nexatech.reporter.client.ui.ReporterSettingsScreen.addSettingsScreen
import dz.nexatech.reporter.util.ui.AbstractDestination
import dz.nexatech.reporter.util.ui.StandardDestinations

object ReporterStandardDestinations : StandardDestinations {

    override val settingsScreen: AbstractDestination get() = ReporterSettingsScreen

    override fun addSettingsScreen(builder: NavGraphBuilder, navController: NavController) =
        builder.addSettingsScreen(navController)
}
