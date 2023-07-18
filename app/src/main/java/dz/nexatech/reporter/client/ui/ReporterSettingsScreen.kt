@file:OptIn(ExperimentalAnimationApi::class)

package dz.nexatech.reporter.client.ui

import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsGroup
import com.google.accompanist.navigation.animation.composable
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.util.model.APPLICATION_THEME
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.DYNAMIC_APPLICATION_THEME
import dz.nexatech.reporter.util.ui.Body
import dz.nexatech.reporter.util.ui.ContentCard
import dz.nexatech.reporter.util.ui.ScrollableColumn
import dz.nexatech.reporter.util.ui.SettingsDivider
import dz.nexatech.reporter.util.ui.SimpleScaffold
import dz.nexatech.reporter.util.ui.StandardAppBar
import dz.nexatech.reporter.util.ui.StaticIcon
import dz.nexatech.reporter.util.ui.StaticScreenDestination
import dz.nexatech.reporter.util.ui.Theme
import dz.nexatech.reporter.util.ui.ThemePicker
import dz.nexatech.reporter.util.ui.Title
import dz.nexatech.reporter.util.ui.contentPadding
import dz.nexatech.reporter.util.ui.zero_padding

object ReporterSettingsScreen : StaticScreenDestination(
    route = "settings",
    icon = StaticIcon.baseline_settings,
    titleRes = R.string.settings,
) {
    fun NavController.toSettingsScreen(navOptions: NavOptions? = null) {
        navigate(this@ReporterSettingsScreen.route, navOptions)
    }

    fun NavGraphBuilder.addSettingsScreen(navController: NavController): ReporterSettingsScreen {
        val thisRoute = this@ReporterSettingsScreen.route
        composable(thisRoute) { ReporterSettingsView(navController) }
        return this@ReporterSettingsScreen
    }

    @Composable
    fun ReporterSettingsView(navController: NavController) {
        SimpleScaffold(
            topBar = {
                StandardAppBar(
                    navController = navController,
                    title = this@ReporterSettingsScreen.titleRes,
                    actions = {},
                )
            },
        ) {
            ScrollableColumn {
                Button({ AppConfig.getState(APPLICATION_THEME).setter("abc") }) {
                    Body("use illegal theme")
                }
                ContentCard(Modifier.contentPadding()) {
                    SettingsGroup(title = { Title("Page layout") }) {

                        val dynamicAppThemeEnabled =
                            AppConfig.getState(DYNAMIC_APPLICATION_THEME)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            SettingsDivider()
                            SettingsCheckbox(
                                state = dynamicAppThemeEnabled,
                                title = { Title("Use System Theme") },
                                subtitle = { Body("Use a theme based on your Wallpaper") },
                            )
                        }

                        val appTheme = AppConfig.getState(APPLICATION_THEME)
                        SettingsDivider()
                        Title(
                            text = "Application Theme:",
                            modifier = Modifier.padding(
                                top = Theme.dimens.content_padding.top * 2,
                                bottom = zero_padding,
                                start = Theme.dimens.content_padding.start * 4,
                                end = Theme.dimens.content_padding.end * 4,
                            ),
                        )
                        ThemePicker(
                            modifier = Modifier.padding(
                                Theme.dimens.content_padding.copy(
                                    start = zero_padding,
                                    end = zero_padding
                                ) * 2
                            ),
                            selectedTheme = appTheme.value,
                            onThemeSelected = { appTheme.value = it.name },
                            enabled = dynamicAppThemeEnabled.value.not(),
                            header = {},
                            footer = {},
                        )
                    }
                }
            }
        }
    }
}