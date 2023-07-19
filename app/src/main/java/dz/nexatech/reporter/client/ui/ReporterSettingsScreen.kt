package dz.nexatech.reporter.client.ui

import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import dz.nexatech.reporter.util.model.rememberLayoutWidth
import dz.nexatech.reporter.util.ui.AnimatedApplicationTheme
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

@OptIn(ExperimentalLayoutApi::class)
object ReporterSettingsScreen : StaticScreenDestination(
    route = "settings",
    icon = StaticIcon.baseline_settings,
    titleRes = R.string.settings,
) {
    fun NavController.toSettingsScreen(navOptions: NavOptions? = null) {
        navigate(this@ReporterSettingsScreen.route, navOptions)
    }

    @OptIn(ExperimentalAnimationApi::class)
    fun NavGraphBuilder.addSettingsScreen(navController: NavController): ReporterSettingsScreen {
        val thisRoute = this@ReporterSettingsScreen.route
        composable(thisRoute) {
            AnimatedApplicationTheme {
                ReporterSettingsView(navController)
            }
        }
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
            val width by rememberLayoutWidth()
            ScrollableColumn {
                ContentCard(
                    Modifier
                        .contentPadding()
                        .width(width)) {
                    SettingsGroup(title = { Title(R.string.app_appearance_settings_group) }) {

                        val dynamicAppThemeEnabled =
                            AppConfig.getState(DYNAMIC_APPLICATION_THEME)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            SettingsDivider()
                            SettingsCheckbox(
                                state = dynamicAppThemeEnabled,
                                title = { Title(R.string.dynamic_app_theme_title) },
                                subtitle = { Body(R.string.dynamic_app_theme_subtitle) },
                            )
                        }

                        val appTheme = AppConfig.getState(APPLICATION_THEME)
                        SettingsDivider()
                        Title(
                            textRes = R.string.app_theme_title,
                            modifier = Modifier.padding(
                                top = Theme.dimens.content_padding.top * 2,
                                bottom = zero_padding,
                                start = Theme.dimens.content_padding.start * 4,
                                end = Theme.dimens.content_padding.end * 4,
                            ),
                        )
                        ThemePicker(
                            modifier = Modifier.padding(Theme.dimens.content_padding * 2),
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