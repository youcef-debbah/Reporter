package dz.nexatech.reporter.util.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.util.model.toggle

@Composable
fun SimpleAppBar(@StringRes title: Int, icon: AbstractIcon? = null) {
    SimpleAppBar(title = stringRes(title), icon = icon)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAppBar(
    title: String = AbstractApplication.INSTANCE.config.applicationName,
    icon: AbstractIcon? = null,
) {
    TopAppBar(
        title = {
            Body(title)
        },
        navigationIcon = {
            if (icon != null) {
                SimpleScreenIcon(icon)
            }
        }
    )
}

@Composable
fun SimpleScreenIcon(icon: AbstractIcon) {
    IconButton(onClick = {}, enabled = false) {
        InfoIcon(icon = icon, desc = null)
    }
}

@Composable
fun StandardAppBar(
    currentRoute: String,
    navController: NavController,
    @StringRes title: Int,
    navigationIcon: @Composable () -> Unit = { StandardBackButton(navController) },
    actions: @Composable RowScope.() -> Unit = {
        StandardAppBarDropdownMenu(
            currentRoute,
            navController
        )
    },
) {
    StandardAppBar(navController, currentRoute, stringRes(title), navigationIcon, actions)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardAppBar(
    navController: NavController,
    currentRoute: String,
    title: String = AbstractApplication.INSTANCE.config.applicationName,
    navigationIcon: @Composable () -> Unit = { StandardBackButton(navController) },
    actions: @Composable RowScope.() -> Unit = {
        StandardAppBarDropdownMenu(
            currentRoute,
            navController
        )
    },
) {
    TopAppBar(
        title = {
            Body(title)
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
fun StandardAppbarIcon(icon: AbstractIcon?) {
    if (icon != null) {
        IconButton(
            enabled = false,
            onClick = {},
        ) {
            DecorativeIcon(icon)
        }
    }
}

@Composable
fun StandardAppBarDropdownMenu(
    currentRoute: String,
    navController: NavController,
    actions: @Composable (MutableState<Boolean>) -> Unit = {},
) {
    val menuExpanded = rememberSaveable { mutableStateOf(false) }
    IconButton(onClick = { menuExpanded.toggle() }) {
        InfoIcon(
            icon = R.drawable.baseline_more_vert_24,
            desc = R.string.icon_desc_open_drop_menu
        )
    }
    DropdownMenu(expanded = menuExpanded.value, onDismissRequest = { menuExpanded.value = false }) {
        val destinations = remember { AbstractApplication.INSTANCE.config.standardDestinations }
        actions(menuExpanded)
        ScreenMenuItem(currentRoute, destinations.settingsScreen, menuExpanded, navController)
        ScreenMenuItem(currentRoute, destinations.helpScreen, menuExpanded, navController)
        ScreenMenuItem(currentRoute, destinations.aboutScreen, menuExpanded, navController)
    }
}

@Composable
private fun ScreenMenuItem(
    currentRoute: String,
    destinationScreen: AbstractDestination,
    menuExpanded: MutableState<Boolean>,
    navController: NavController,
) {
    if (currentRoute != destinationScreen.route) {
        DropdownMenuTextItem(
            destinationScreen.title(),
            destinationScreen.icon
        ) {
            menuExpanded.value = false
            navController.navigate(destinationScreen)
        }
    }
}

@Composable
fun DropdownMenuTextItem(
    @StringRes title: Int,
    icon: AbstractIcon?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit,
) {
    DropdownMenuTextItem(
        stringRes(title),
        icon,
        modifier,
        enabled,
        colors,
        contentPadding,
        interactionSource,
        onClick
    )
}

@Composable
fun DropdownMenuTextItem(
    title: String,
    icon: AbstractIcon?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Body(text = title, style = Theme.typography.titleSmall) },
        leadingIcon = icon?.let { { DecorativeIcon(icon) } },
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        onClick = onClick,
    )
}