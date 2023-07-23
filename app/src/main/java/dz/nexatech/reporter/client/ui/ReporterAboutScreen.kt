package dz.nexatech.reporter.client.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.model.COMPANY_WEBSITE
import dz.nexatech.reporter.client.model.CONTACT_EMAIL
import dz.nexatech.reporter.client.model.MAX_LAYOUT_COLUMN_WIDTH
import dz.nexatech.reporter.client.model.MIN_LAYOUT_COLUMN_WIDTH
import dz.nexatech.reporter.client.model.SOURCE_CODE_URL
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.ui.Body
import dz.nexatech.reporter.util.ui.CentredRow
import dz.nexatech.reporter.util.ui.ContentCard
import dz.nexatech.reporter.util.ui.DecorativeIcon
import dz.nexatech.reporter.util.ui.ExternalLink
import dz.nexatech.reporter.util.ui.PaddedColumn
import dz.nexatech.reporter.util.ui.SimpleScaffold
import dz.nexatech.reporter.util.ui.StandardAppBar
import dz.nexatech.reporter.util.ui.StaticIcon
import dz.nexatech.reporter.util.ui.StaticScreenDestination
import dz.nexatech.reporter.util.ui.Theme
import dz.nexatech.reporter.util.ui.ThemedLink
import dz.nexatech.reporter.util.ui.Title
import dz.nexatech.reporter.util.ui.contentPadding
import dz.nexatech.reporter.util.ui.stringRes
import dz.nexatech.reporter.util.ui.textPadding
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
            ContentCard(
                Modifier
                    .padding(Theme.dimens.content_padding * 2)
                    .sizeIn(
                        minWidth = AppConfig.get(MIN_LAYOUT_COLUMN_WIDTH).dp,
                        maxWidth = AppConfig.get(MAX_LAYOUT_COLUMN_WIDTH).dp
                    )
            ) {
                PaddedColumn {
                    Title(R.string.app_name)
                    VerticalSpacer()

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DecorativeIcon(
                            icon = StaticIcon.company_icon,
                            modifier = Modifier
                                .contentPadding()
                                .size(80.dp),
                            tint = Theme.colorScheme.primary
                        )
                        val text = rememberCompanyDesc()
                        Text(
                            text = text,
                            modifier = Modifier.textPadding(),
                            textAlign = TextAlign.Center
                        )
                    }
                    VerticalSpacer()

                    CentredRow {
                        ContactUsLink()
                        ThemedLink(R.string.website, R.drawable.baseline_language_24) {
                            ExternalLink.openLink(AppConfig.get(COMPANY_WEBSITE))
                        }
                        ThemedLink(R.string.source_code, R.drawable.baseline_code_24) {
                            ExternalLink.openLink(AppConfig.get(SOURCE_CODE_URL))
                        }
                    }
                    VerticalSpacer(1.5f)

                    Body("This software is published under", style = Theme.typography.labelLarge)
                    ThemedLink(text = "The GNU General Public License (v3.0)") {
                        ExternalLink.openLink("https://www.gnu.org/licenses/gpl-3.0-standalone.html")
                    }
                    VerticalSpacer()
                }
            }
        }
    }
}

@Composable
private fun VerticalSpacer(scale: Float = 1f) {
    val height = Theme.dimens.content_padding.top + Theme.dimens.content_padding.bottom
    Spacer(modifier = Modifier.height(height * scale))
}

val companyNameStyle = SpanStyle(fontWeight = FontWeight.Bold)

@Composable
private fun rememberCompanyDesc(): AnnotatedString {
    val desc1 = stringRes(R.string.company_desc_1)
    val companyName = stringRes(R.string.company_name)
    val desc2 = stringRes(R.string.company_desc_2)
    val text = remember(desc1, companyName, desc2) {
        AnnotatedString.Builder(128).apply {
            append(desc1)
            append(' ')
            append(companyName)
            addStyle(companyNameStyle, desc1.length + 1, this.length)
            append(' ')
            append(desc2)
        }.toAnnotatedString()
    }
    return text
}

@Composable
fun ContactUsLink() {
    ThemedLink(R.string.contact_us, R.drawable.baseline_email_24) {
        ExternalLink.openEmailDialer(AppConfig.get(CONTACT_EMAIL))
    }
}