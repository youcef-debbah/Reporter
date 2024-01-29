package dz.nexatech.reporter.client.ui

import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsSlider
import androidx.navigation.compose.composable
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.model.MAX_LAYOUT_COLUMN_WIDTH
import dz.nexatech.reporter.client.model.MIN_LAYOUT_COLUMN_WIDTH
import dz.nexatech.reporter.util.model.APPLICATION_THEME
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.DYNAMIC_APPLICATION_THEME
import dz.nexatech.reporter.util.model.IntConfigState
import dz.nexatech.reporter.util.model.rememberLayoutWidth
import dz.nexatech.reporter.util.ui.AnimatedApplicationTheme
import dz.nexatech.reporter.util.ui.Body
import dz.nexatech.reporter.util.ui.ContentCard
import dz.nexatech.reporter.util.ui.DecorativeIcon
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
import dz.nexatech.reporter.util.ui.small_padding
import dz.nexatech.reporter.util.ui.stringRes

@OptIn(ExperimentalLayoutApi::class)
object ReporterSettingsScreen : StaticScreenDestination(
    route = "settings",
    icon = StaticIcon.baseline_settings,
    titleRes = R.string.settings,
) {
    fun NavController.toSettingsScreen(navOptions: NavOptions? = null) {
        navigate(this@ReporterSettingsScreen.route, navOptions)
    }

    fun NavGraphBuilder.addSettingsScreen(navController: NavController): ReporterSettingsScreen {
        composable(this@ReporterSettingsScreen.route) {
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
                    currentRoute = this@ReporterSettingsScreen.route,
                    navController = navController,
                    title = this@ReporterSettingsScreen.titleRes,
                )
            },
        ) {
            val width by rememberLayoutWidth()
            ScrollableColumn {
                ContentCard(
                    Modifier
                        .contentPadding()
                        .width(width)
                ) {
                    SettingsGroup(title = { Title(R.string.app_appearance_settings_group) }) {

                        val dynamicAppThemeEnabled =
                            AppConfig.getState(DYNAMIC_APPLICATION_THEME)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            SettingsDivider()
                            SettingsCheckbox(
                                icon = { DecorativeIcon(icon = R.drawable.baseline_color_lens_24) },
                                state = dynamicAppThemeEnabled,
                                title = {
                                    Title(
                                        R.string.dynamic_app_theme_title,
                                        textAlign = TextAlign.Start
                                    )
                                },
                                subtitle = {
                                    Body(
                                        R.string.dynamic_app_theme_subtitle,
                                        textAlign = TextAlign.Start
                                    )
                                },
                            )
                        }

                        val appTheme = AppConfig.getState(APPLICATION_THEME)
                        SettingsDivider(Modifier.padding(bottom = Theme.dimens.content_padding.bottom * 2))
                        Row {
                            Box(
                                Modifier
                                    .width(56.dp)
                                    .padding(top = Theme.dimens.content_padding.top),
                                Alignment.Center
                            ) {
                                DecorativeIcon(icon = R.drawable.baseline_color_lens_24)
                            }
                            Title(
                                textRes = R.string.app_theme_title,
                            )
                        }
                        ThemePicker(
                            modifier = Modifier.padding(Theme.dimens.content_padding * 2),
                            selectedTheme = appTheme.value,
                            onThemeSelected = { appTheme.value = it.name },
                            enabled = dynamicAppThemeEnabled.value.not(),
                            header = {},
                            footer = {},
                        )

                        val maxColumnWidth = AppConfig.getState(MAX_LAYOUT_COLUMN_WIDTH)
                        SettingsDivider()
                        SliderSetting(
                            maxColumnWidth,
                            350f..900f,
                            stringRes(R.string.max_column_width, maxColumnWidth.value),
                            R.drawable.ms_width_24
                        )

                        val minColumnWidth = AppConfig.getState(MIN_LAYOUT_COLUMN_WIDTH)
                        SettingsDivider()
                        SliderSetting(
                            minColumnWidth,
                            200f..350f,
                            stringRes(R.string.min_column_width, minColumnWidth.value),
                            R.drawable.ms_width_24
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SliderSetting(
        state: IntConfigState,
        range: ClosedFloatingPointRange<Float>,
        title: String,
        iconRes: Int,
    ) {
        Spacer(modifier = Modifier.height(Theme.dimens.content_padding.top))
        SettingsSlider(
            modifier = Modifier
                .requiredHeight(90.dp)
                .padding(
                    start = small_padding,
                    end = small_padding,
                ),
            sliderModifier = Modifier.padding(
                start = small_padding,
                end = small_padding,
            ),
            title = {
                Row {
                    Box(
                        Modifier
                            .width(38.dp)
                            .padding(top = Theme.dimens.content_padding.top)
                            .offset((-10).dp),
                        Alignment.Center
                    ) {
                        DecorativeIcon(icon = iconRes)
                    }
                    Title(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                    )
                }
            },
            valueRange = range,
            state = state.floatState,
        )
    }
}