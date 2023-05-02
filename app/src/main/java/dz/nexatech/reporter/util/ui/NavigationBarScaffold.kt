@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalMaterialNavigationApi::class
)

package dz.nexatech.reporter.util.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import dz.nexatech.reporter.client.model.REMOTE_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED
import dz.nexatech.reporter.client.common.indexDiff
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.REMOTE_NAVIGATION_ANIMATION_DURATION

@Composable
fun NavigationBarScaffold(
    startDestination: AbstractDestination,
    modifier: Modifier = Modifier,
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberAnimatedNavController(bottomSheetNavigator),
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = { DefaultNavigationBar(navController) },
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    builder: NavGraphBuilder.(NavHostController) -> Unit,
) {
    ApplicationTheme {
        StatelessNavigationBarScaffold(
            startDestination,
            bottomSheetNavigator,
            navController,
            modifier,
            topBar,
            bottomBar,
            snackbarHost,
            floatingActionButton,
            floatingActionButtonPosition,
            containerColor,
            contentColor,
            builder
        )
    }
}

@Composable
private fun StatelessNavigationBarScaffold(
    startDestination: AbstractDestination,
    bottomSheetNavigator: BottomSheetNavigator,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = { DefaultNavigationBar(navController) },
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    builder: NavGraphBuilder.(NavHostController) -> Unit
) {
    ModalBottomSheetLayout(bottomSheetNavigator) {
        Scaffold(
            modifier = modifier,
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            containerColor = containerColor,
            contentColor = contentColor,
            content = { innerPadding ->
                val destinations = navController.currentScreen().destinationsOrEmpty()
                AnimatedNavHost(
                    navController = navController,
                    startDestination = startDestination.route,
                    modifier = Modifier.padding(innerPadding),
                    builder = { builder.invoke(this, navController) },
                    enterTransition = {
                        if (AppConfig.get(REMOTE_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED)) {
                            val diff = destinations.iterator().indexDiff(
                                initialState.destination::isLinkedTo,
                                targetState.destination::isLinkedTo,
                            )

                            if (diff == null || diff == 0) {
                                defaultInTransition(false)
                            } else {
                                inTransition(diff > 0)
                            }
                        } else {
                            EnterTransition.None
                        }
                    },
                    exitTransition = {
                        if (AppConfig.get(REMOTE_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED)) {
                            val diff = destinations.iterator().indexDiff(
                                initialState.destination::isLinkedTo,
                                targetState.destination::isLinkedTo,
                            )

                            if (diff == null || diff == 0) {
                                defaultOutTransition(false)
                            } else {
                                outTransition(diff < 0)
                            }
                        } else {
                            ExitTransition.None
                        }
                    },
                    popEnterTransition = {
                        if (AppConfig.get(REMOTE_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED)) {
                            val diff = destinations.iterator().indexDiff(
                                initialState.destination::isLinkedTo,
                                targetState.destination::isLinkedTo,
                            )

                            if (diff == null || diff == 0) {
                                defaultInTransition(true)
                            } else {
                                inTransition(diff > 0)
                            }
                        } else {
                            EnterTransition.None
                        }
                    },
                    popExitTransition = {
                        if (AppConfig.get(REMOTE_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED)) {
                            val diff = destinations.iterator().indexDiff(
                                initialState.destination::isLinkedTo,
                                targetState.destination::isLinkedTo,
                            )

                            if (diff == null || diff == 0) {
                                defaultOutTransition(true)
                            } else {
                                outTransition(diff < 0)
                            }
                        } else {
                            ExitTransition.None
                        }
                    },
                )

            }
        )
    }
}

@Suppress("UNUSED_PARAMETER")
private fun defaultInTransition(pop: Boolean): EnterTransition {
    return fadeIn(animationSpec = tween(AppConfig.get(REMOTE_NAVIGATION_ANIMATION_DURATION)))
}

@Suppress("UNUSED_PARAMETER")
private fun defaultOutTransition(pop: Boolean): ExitTransition {
    return fadeOut(animationSpec = tween(AppConfig.get(REMOTE_NAVIGATION_ANIMATION_DURATION)))
}

//private fun AnimatedContentScope<NavBackStackEntry>.defaultInTransition(pop: Boolean): EnterTransition {
//    return slideIntoContainer(
//        if (pop) AnimatedContentScope.SlideDirection.Down else AnimatedContentScope.SlideDirection.Up,
//        animationSpec = tween(AppConfig.get(RemoteInt.NAVIGATION_ANIMATION_DURATION))
//    )
//}
//
//private fun AnimatedContentScope<NavBackStackEntry>.defaultOutTransition(pop: Boolean): ExitTransition {
//    return slideOutOfContainer(
//        if (pop) AnimatedContentScope.SlideDirection.Down else AnimatedContentScope.SlideDirection.Up,
//        animationSpec = tween(AppConfig.get(RemoteInt.NAVIGATION_ANIMATION_DURATION))
//    )
//}

private fun AnimatedContentScope<NavBackStackEntry>.inTransition(goingDeeper: Boolean): EnterTransition =
    slideIntoContainer(
        if (goingDeeper) AnimatedContentScope.SlideDirection.Start else AnimatedContentScope.SlideDirection.End,
        animationSpec = tween(AppConfig.get(REMOTE_NAVIGATION_ANIMATION_DURATION))
    )

private fun AnimatedContentScope<NavBackStackEntry>.outTransition(goingDeeper: Boolean): ExitTransition =
    slideOutOfContainer(
        if (goingDeeper) AnimatedContentScope.SlideDirection.End else AnimatedContentScope.SlideDirection.Start,
        animationSpec = tween(AppConfig.get(REMOTE_NAVIGATION_ANIMATION_DURATION))
    )

@Composable
fun DefaultNavigationBar(navController: NavHostController) {
    val currentScreen = navController.currentScreen()
    val destinations = currentScreen.destinationsOrEmpty()
    AnimatedVisibility(destinations.isNotEmpty()) {
        NavigationBar {
            destinations.forEach { screen ->
                NavigationBarItem(
                    icon = { InfoIcon(screen.icon, screen.title()) },
                    label = screen.label()?.let { { ThemedText(it) } },
                    selected = screen == currentScreen,
                    onClick = {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

fun NavDestination?.isLinkedTo(abstractDestination: AbstractDestination) =
    isLinkedTo(abstractDestination.route)

fun NavDestination?.isLinkedTo(route: String) = this?.hierarchy?.any { it.route == route } == true