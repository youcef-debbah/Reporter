@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.reporter.client.R

@Composable
fun SimpleAppBar(@StringRes title: Int, @DrawableRes icon: Int? = null) {
    SimpleAppBar(title = stringRes(title), icon = icon)
}

@Composable
fun SimpleAppBar(title: String = AbstractApplication.INSTANCE.config.applicationName, @DrawableRes icon: Int? = null) {
    TopAppBar(
        title = {
            ThemedText(title)
        },
        navigationIcon = {
            SimpleScreenIcon(icon)
        }
    )
}

@Composable
fun SimpleScreenIcon(@DrawableRes icon: Int?) {
    if (icon != null) {
        IconButton(onClick = {}, enabled = false) {
            VectorIcon(icon = icon, desc = null)
        }
    }
}

@Composable
fun StandardAppBar(
    navController: NavController,
    @StringRes title: Int,
    actions: @Composable RowScope.() -> Unit = { StandardAppBarActions(navController) },
    navigationIcon: @Composable () -> Unit = { StandardBackButton(navController) },
) {
    StandardAppBar(navController, stringRes(title), actions, navigationIcon)
}

@Composable
fun StandardAppBar(
    navController: NavController,
    title: String = AbstractApplication.INSTANCE.config.applicationName,
    actions: @Composable RowScope.() -> Unit = { StandardAppBarActions(navController) },
    navigationIcon: @Composable () -> Unit = { StandardBackButton(navController) },
) {
    TopAppBar(
        title = {
            ThemedText(title)
        },
        navigationIcon = navigationIcon,
        actions = actions,
    )
}

@Composable
fun StandardBackButton(navController: NavController) {
    IconButton(onClick = { navController.popBackStack() }) {
        VectorIcon(
            icon = R.drawable.baseline_arrow_back_24,
            desc = R.string.back
        )
    }
}

@Composable
private fun StandardAppBarActions(
    navController: NavController,
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    IconButton(onClick = { menuExpanded = menuExpanded.not() }) {
        VectorIcon(
            icon = R.drawable.baseline_more_vert_24,
            desc = R.string.icon_desc_open_drop_menu
        )
    }
    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
        val destinations = AbstractApplication.INSTANCE.config.standardDestinations
        DropdownMenuItem(
            leadingIcon = { VectorIcon(icon = destinations.settingsScreen.icon, desc = null) },
            text = { ThemedText(destinations.settingsScreen.title) },
            onClick = {
                menuExpanded = false
                navController.navigate(destinations.settingsScreen.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
        )
    }
}

fun NavGraphBuilder.addStandardAppBarScreens(navController: NavController) {
    val destinations = AbstractApplication.INSTANCE.config.standardDestinations
    composable(destinations.settingsScreen.route) {
        destinations.SettingsScreenView(navController)
    }
}