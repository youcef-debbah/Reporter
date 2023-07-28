package dz.nexatech.reporter.client.ui

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.backgroundLaunch
import dz.nexatech.reporter.client.model.WEB_COLOR_PICKER_URL
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.ui.Body
import dz.nexatech.reporter.util.ui.CentredRow
import dz.nexatech.reporter.util.ui.ContentCard
import dz.nexatech.reporter.util.ui.ExternalLink
import dz.nexatech.reporter.util.ui.InfoIcon
import dz.nexatech.reporter.util.ui.PaddedDivider
import dz.nexatech.reporter.util.ui.ScrollableColumn
import dz.nexatech.reporter.util.ui.SimpleScaffold
import dz.nexatech.reporter.util.ui.StandardAppBar
import dz.nexatech.reporter.util.ui.StaticIcon
import dz.nexatech.reporter.util.ui.StaticScreenDestination
import dz.nexatech.reporter.util.ui.Theme
import dz.nexatech.reporter.util.ui.ThemedLink
import dz.nexatech.reporter.util.ui.Title
import dz.nexatech.reporter.util.ui.contentPadding
import dz.nexatech.reporter.util.ui.defaultDensity
import dz.nexatech.reporter.util.ui.indentedTextPadding
import dz.nexatech.reporter.util.ui.stringRes
import dz.nexatech.reporter.util.ui.textPadding
import dz.nexatech.reporter.util.ui.themedComposable
import dz.nexatech.reporter.util.ui.toPixels
import kotlinx.coroutines.CoroutineScope

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
        val scope = rememberCoroutineScope()
        val scrollState = rememberScrollState()
        val scrollPosition00 = remember { mutableStateOf(0f) }

        val screenHeightDp = LocalConfiguration.current.screenHeightDp
        val showFab = remember(screenHeightDp) {
            val threshold = screenHeightDp.toPixels(defaultDensity * 1.75f)
            derivedStateOf(structuralEqualityPolicy()) { scrollState.value > threshold }
        }

        SimpleScaffold(
            topBar = {
                StandardAppBar(
                    navController,
                    this@ReporterHelpScreen.route,
                    this@ReporterHelpScreen.title(),
                )
            },
            floatingActionButton = {
                if (showFab.value) {
                    FloatingActionButton(onClick = {
                        scope.backgroundLaunch {
                            scrollState.animateScrollTo(scrollPosition00.value.toInt())
                        }
                    }) {
                        InfoIcon(
                            icon = R.drawable.baseline_keyboard_arrow_up_24,
                            desc = R.string.scroll_to_top_desc
                        )
                    }
                }
            }
        ) {
            val scrollPosition01 = remember { mutableStateOf(0f) }
            val scrollPosition02 = remember { mutableStateOf(0f) }
            val scrollPosition03 = remember { mutableStateOf(0f) }
            val scrollPosition04 = remember { mutableStateOf(0f) }
            ScrollableColumn(scrollState = scrollState) {
                HelpCard(R.string.help_page_index, scrollPosition00) {
                    IndexLink(R.string.help_page_title_01, scope, scrollState, scrollPosition01)
                    IndexLink(R.string.help_page_title_02, scope, scrollState, scrollPosition02)
                    IndexLink(R.string.help_page_title_03, scope, scrollState, scrollPosition03)
                    IndexLink(R.string.help_page_title_04, scope, scrollState, scrollPosition04)
                }

                HelpCard(R.string.help_page_title_01, scrollPosition01) {
                    Body(R.string.help_page_title_01_section_01, textAlign = TextAlign.Start)
                    HelpSpacer()
                    Body(R.string.help_page_title_01_section_02, textAlign = TextAlign.Start)
                    HelpSpacer()
                    Body(R.string.help_page_title_01_section_03, textAlign = TextAlign.Start)
                    Body(
                        stringRes(
                            R.string.help_page_title_01_section_04,
                            stringRes(R.string.download_template_menu_item)
                        ),
                        Modifier.indentedTextPadding(), textAlign = TextAlign.Start
                    )
                    Body(
                        R.string.help_page_title_01_section_05,
                        Modifier.indentedTextPadding(),
                        textAlign = TextAlign.Start
                    )
                    Body(
                        R.string.help_page_title_01_section_06,
                        Modifier.indentedTextPadding(),
                        textAlign = TextAlign.Start
                    )
                    Body(
                        R.string.help_page_title_01_section_07,
                        Modifier.indentedTextPadding(),
                        textAlign = TextAlign.Start
                    )
                    HelpSpacer()
                    Body(
                        stringRes(
                            R.string.help_page_title_01_section_08,
                            stringRes(R.string.export_pdf_button_label)
                        ), textAlign = TextAlign.Start
                    )
                    HelpSpacer()
                    Body(R.string.help_page_title_01_section_09, textAlign = TextAlign.Start)
                }

                HelpCard(R.string.help_page_title_02, scrollPosition02) {
                    Body(R.string.help_page_title_02_section_01, textAlign = TextAlign.Start)
                    HelpSpacer()
                    Body(R.string.help_page_title_02_section_02, textAlign = TextAlign.Start)
                    CentredRow(horizontalArrangement = Arrangement.Start) {
                        Body(R.string.help_page_title_02_section_03)
                        IndexLink(R.string.help_page_title_04, scope, scrollState, scrollPosition04)
                    }
                }

                HelpCard(R.string.help_page_title_03, scrollPosition03) {
                    Body(R.string.help_page_title_03_section_01, textAlign = TextAlign.Start)
                    HelpSpacer()
                    Body(R.string.help_page_title_03_section_02, textAlign = TextAlign.Start)
                    HelpSpacer()
                    Body(R.string.help_page_title_03_section_03, textAlign = TextAlign.Start)
                    HelpSpacer()
                    Body(
                        R.string.help_page_title_03_section_04,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    )
                    Body(R.string.help_page_title_03_section_05, textAlign = TextAlign.Start)
                    Body(R.string.help_page_title_03_section_06, textAlign = TextAlign.Start)
                    CentredRow(horizontalArrangement = Arrangement.Start) {
                        Body(
                            R.string.help_page_title_03_section_07,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Start
                        )
                        ThemedLink(
                            R.string.help_page_title_03_section_08,
                            R.drawable.baseline_open_in_browser_24
                        ) {
                            ExternalLink.openLink(AppConfig.get(WEB_COLOR_PICKER_URL))
                        }
                    }
                }

                HelpCard(R.string.help_page_title_04, scrollPosition04) {
                    Body(R.string.help_page_title_04_section_01, textAlign = TextAlign.Start)
                    HelpSpacer()
                    Body(R.string.help_page_title_04_section_02, textAlign = TextAlign.Start)
                    HelpSpacer()
                    Body(R.string.help_page_title_04_section_03, textAlign = TextAlign.Start)
                    HelpSpacer()
                    Body(R.string.help_page_title_04_section_04, textAlign = TextAlign.Start)
                    HelpSpacer()
                    Body(R.string.help_page_title_04_section_05, textAlign = TextAlign.Start)
                }
            }
        }
    }

    @Composable
    private fun HelpSpacer() {
        Spacer(
            modifier = Modifier.height(
                Theme.dimens.content_padding.top + Theme.dimens.content_padding.bottom
            )
        )
    }
}

@Composable
private fun IndexLink(
    @StringRes titleRes: Int,
    scope: CoroutineScope,
    scrollState: ScrollState,
    scrollPosition: MutableState<Float>,
) {
    ThemedLink(titleRes) {
        scope.backgroundLaunch {
            scrollState.animateScrollTo(scrollPosition.value.toInt())
        }
    }
}

@Composable
private fun HelpCard(
    @StringRes titleRes: Int,
    scrollPosition: MutableState<Float>,
    content: @Composable () -> Unit,
) {
    ContentCard(
        Modifier
            .contentPadding()
            .onGloballyPositioned { coordinates ->
                scrollPosition.value = coordinates.positionInParent().y
            }) {
        Column(Modifier.contentPadding()) {
            Title(
                titleRes,
                Modifier
                    .textPadding()
                    .padding(Theme.dimens.content_padding)
            )
            PaddedDivider()
            Column(Modifier.padding(Theme.dimens.content_padding * 2)) {
                content()
            }
        }
    }
}