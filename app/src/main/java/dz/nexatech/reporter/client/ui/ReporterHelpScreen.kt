package dz.nexatech.reporter.client.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.util.ui.Body
import dz.nexatech.reporter.util.ui.PaddedColumn
import dz.nexatech.reporter.util.ui.SimpleScaffold
import dz.nexatech.reporter.util.ui.StandardAppBar
import dz.nexatech.reporter.util.ui.StaticIcon
import dz.nexatech.reporter.util.ui.StaticScreenDestination
import dz.nexatech.reporter.util.ui.themedComposable

object ReporterHelpScreen : StaticScreenDestination(
    route = "reporter_help",
    icon = StaticIcon.baseline_help,
    titleRes = R.string.reporter_help_title,
) {
    fun NavController.toReporterHelpScreen(navOptions: NavOptions? = null) {
        navigate(this@ReporterHelpScreen.route, navOptions)
    }

    @OptIn(ExperimentalAnimationApi::class)
    fun NavGraphBuilder.addReporterHelpScreen(navController: NavController): ReporterHelpScreen {
        themedComposable(this@ReporterHelpScreen.route) {
            ReporterHelpView(navController)
        }
        return this@ReporterHelpScreen
    }

    @Composable
    private fun ReporterHelpView(navController: NavController) {
        SimpleScaffold(
            topBar = {
                StandardAppBar(
                    navController,
                    this@ReporterHelpScreen.route,
                    this@ReporterHelpScreen.title(),
                )
            },
        ) {
            PaddedColumn {
                Body("ReporterHelp")//TODO
            }
        }
    }
}