package dz.nexatech.reporter.client.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.util.ui.Body
import dz.nexatech.reporter.util.ui.StaticScreenDestination
import dz.nexatech.reporter.util.ui.PaddedColumn
import dz.nexatech.reporter.util.ui.SimpleAppBar
import dz.nexatech.reporter.util.ui.SimpleScaffold
import dz.nexatech.reporter.util.ui.StandardAppBar
import dz.nexatech.reporter.util.ui.StaticIcon
import dz.nexatech.reporter.util.ui.themedComposable

object ReporterAboutScreen : StaticScreenDestination(
    route = "reporter_about",
    icon = StaticIcon.baseline_info,
    titleRes = R.string.reporter_about_title,
) {
    fun NavController.toReporterAboutScreen(navOptions: NavOptions? = null) {
        navigate(this@ReporterAboutScreen.route, navOptions)
    }

    @OptIn(ExperimentalAnimationApi::class)
    fun NavGraphBuilder.addReporterAboutScreen(navController: NavController): ReporterAboutScreen {
        themedComposable(this@ReporterAboutScreen.route) {
            ReporterAboutView(navController)
        }
        return this@ReporterAboutScreen
    }

    @Composable
    private fun ReporterAboutView(navController: NavController) {
        SimpleScaffold(
            topBar = {
                StandardAppBar(
                    navController,
                    this@ReporterAboutScreen.route,
                    this@ReporterAboutScreen.title()
                )
            },
        ) {
            PaddedColumn {
                Body("ReporterAbout")//TODO
            }
        }
    }
}