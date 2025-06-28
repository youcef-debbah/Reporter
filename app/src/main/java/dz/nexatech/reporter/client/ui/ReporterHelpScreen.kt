package dz.nexatech.reporter.client.ui

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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
import kotlinx.serialization.Serializable

@Serializable
object ReporterHelpScreen : StaticScreenDestination(
    screenRoute = "reporter_help",
    screenIcon = StaticIcon.baseline_help,
    titleRes = R.string.reporter_help_title,
) {
    fun NavController.toReporterHelpScreen(navOptions: NavOptions? = null) {
        navigate(this@ReporterHelpScreen.route, navOptions)
    }

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
        val scrollPosition00 = remember { mutableIntStateOf(0) }

        val screenHeightDp = LocalConfiguration.current.screenHeightDp
        val showFab = remember(screenHeightDp) {
            val threshold = screenHeightDp.toPixels(defaultDensity * 0.75f)
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
                Box(Modifier.size(56.dp)) {
                    AnimatedVisibility(showFab.value) {
                        FloatingActionButton(onClick = {
                            scope.backgroundLaunch {
                                scrollState.animateScrollTo(scrollPosition00.intValue)
                            }
                        }) {
                            InfoIcon(
                                icon = R.drawable.baseline_keyboard_arrow_up_24,
                                desc = R.string.scroll_to_top_desc
                            )
                        }
                    }
                }
            }
        ) {
            val scrollPosition01 = remember { mutableIntStateOf(0) }
            val scrollPosition02 = remember { mutableIntStateOf(0) }
            val scrollPosition03 = remember { mutableIntStateOf(0) }
            val scrollPosition04 = remember { mutableIntStateOf(0) }
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
                        stringRes(
                            R.string.help_page_title_01_section_06,
                            stringRes(R.string.import_template_menu_item)
                        ),
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
                    ContactUsLink()
                }

                HelpCard(R.string.help_page_title_02, scrollPosition02) {
                    Body(R.string.help_page_title_02_section_01, textAlign = TextAlign.Start)
                    HelpSpacer()
                    Body(R.string.help_page_title_02_section_02, textAlign = TextAlign.Start)
                    CentredRow(horizontalArrangement = Arrangement.Start) {
                        Body(R.string.help_page_title_02_section_03, textAlign = TextAlign.Start)
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
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Body(R.string.help_page_title_04_section_03, Modifier.fillMaxWidth())
                    }
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
    scrollPosition: MutableIntState
) {
    ThemedLink(titleRes) {
        scope.backgroundLaunch {
            scrollState.animateScrollTo(scrollPosition.intValue)
        }
    }
}

@Composable
private fun HelpCard(
    @StringRes titleRes: Int,
    scrollPosition: MutableIntState,
    content: @Composable () -> Unit,
) {
    ContentCard(
        Modifier
            .contentPadding()
            .onGloballyPositioned { coordinates ->
                scrollPosition.intValue = coordinates.positionInParent().y.toInt()
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