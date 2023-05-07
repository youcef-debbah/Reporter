@file:OptIn(ExperimentalMaterial3Api::class)

package dz.nexatech.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.common.collect.ImmutableList
import dz.nexatech.reporter.client.R

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
            InfoIcon(icon = icon, desc = null)
        }
    }
}

@Composable
fun StandardAppBar(
    navController: NavController,
    @StringRes title: Int,
    navigationIcon: @Composable () -> Unit = { StandardBackButton(navController) },
    actions: @Composable RowScope.() -> Unit = { StandardAppBarDropdownMenu(navController) },
) {
    StandardAppBar(navController, stringRes(title), navigationIcon, actions)
}

@Composable
fun StandardAppBar(
    navController: NavController,
    title: String = AbstractApplication.INSTANCE.config.applicationName,
    navigationIcon: @Composable () -> Unit = { StandardBackButton(navController) },
    actions: @Composable RowScope.() -> Unit = { StandardAppBarDropdownMenu(navController) },
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
        InfoIcon(
            icon = R.drawable.baseline_arrow_back_24,
            desc = R.string.back
        )
    }
}

@Composable
fun StandardAppBarDropdownMenu(
    navController: NavController,
    actions: @Composable (MutableState<Boolean>) -> Unit = {},
) {
    val menuExpanded = rememberSaveable { mutableStateOf(false) }
    IconButton(onClick = { menuExpanded.value = menuExpanded.value.not() }) {
        InfoIcon(
            icon = R.drawable.baseline_more_vert_24,
            desc = R.string.icon_desc_open_drop_menu
        )
    }
    DropdownMenu(expanded = menuExpanded.value, onDismissRequest = { menuExpanded.value = false }) {
        val destinations = remember { AbstractApplication.INSTANCE.config.standardDestinations }

        actions(menuExpanded)

        DropdownMenuItem(
            leadingIcon = { DecorativeIcon(destinations.settingsScreen.icon) },
            text = { ThemedText(destinations.settingsScreen.title()) },
            onClick = {
                menuExpanded.value = false
                navController.navigate(destinations.settingsScreen.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
        )
    }
}