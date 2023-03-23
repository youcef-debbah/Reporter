package com.reporter.util.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

interface StandardDestinations {

    val settingsScreen: AbstractDestination

    @Composable
    fun SettingsScreenView(navController: NavController)
}