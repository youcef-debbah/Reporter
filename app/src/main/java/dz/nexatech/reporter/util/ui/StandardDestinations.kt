package dz.nexatech.reporter.util.ui

import androidx.compose.runtime.Immutable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

@Immutable
interface StandardDestinations {

    val settingsScreen: AbstractDestination

    fun addSettingsScreen(builder: NavGraphBuilder, navController: NavController)
}