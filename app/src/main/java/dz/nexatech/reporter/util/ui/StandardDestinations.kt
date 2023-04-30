package dz.nexatech.reporter.util.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface StandardDestinations {

    val settingsScreen: AbstractDestination

    fun addSettingsScreen(builder: NavGraphBuilder, navController: NavController)
}