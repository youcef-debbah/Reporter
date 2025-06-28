@file:OptIn(ExperimentalFoundationApi::class)

package dz.nexatech.reporter.client.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
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
import dz.nexatech.reporter.client.model.PRIVACY_POLICY_URL
import dz.nexatech.reporter.client.model.SOURCE_CODE_URL
import dz.nexatech.reporter.util.model.APP_DOWNLOAD_LINK
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.INSTALLATION_ID
import dz.nexatech.reporter.util.model.LATEST_VERSION_NAME
import dz.nexatech.reporter.util.model.Localizer
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.Body
import dz.nexatech.reporter.util.ui.SurroundedLink
import dz.nexatech.reporter.util.ui.CentredRow
import dz.nexatech.reporter.util.ui.ContentCard
import dz.nexatech.reporter.util.ui.DecorativeIcon
import dz.nexatech.reporter.util.ui.ExternalLink
import dz.nexatech.reporter.util.ui.PaddedColumn
import dz.nexatech.reporter.util.ui.PaddedDivider
import dz.nexatech.reporter.util.ui.PaddedRow
import dz.nexatech.reporter.util.ui.ScrollableColumn
import dz.nexatech.reporter.util.ui.SimpleScaffold
import dz.nexatech.reporter.util.ui.StandardAppBar
import dz.nexatech.reporter.util.ui.StaticIcon
import dz.nexatech.reporter.util.ui.StaticScreenDestination
import dz.nexatech.reporter.util.ui.Theme
import dz.nexatech.reporter.util.ui.ThemedLink
import dz.nexatech.reporter.util.ui.Title
import dz.nexatech.reporter.util.ui.Toasts
import dz.nexatech.reporter.util.ui.contentPadding
import dz.nexatech.reporter.util.ui.rememberTextWithLink
import dz.nexatech.reporter.util.ui.stringRes
import dz.nexatech.reporter.util.ui.themedComposable
import kotlinx.serialization.Serializable

val clipboardManager by lazy {
    AbstractApplication.INSTANCE.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
}

@Serializable
object ReporterAboutScreen : StaticScreenDestination(
    screenRoute = "reporter_about",
    screenIcon = StaticIcon.baseline_info,
    titleRes = R.string.reporter_about_title,
) {
    private val centredTextStyle = TextStyle(textAlign = TextAlign.Center)

    fun NavController.toReporterAboutScreen(navOptions: NavOptions? = null) {
        navigate(this@ReporterAboutScreen.route, navOptions)
    }

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
            ScrollableColumn {
                AboutCard(R.string.app_info_title) {
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
                        val text: AnnotatedString = rememberTextWithLink(
                            prefix = R.string.company_desc_prefix,
                            label = R.string.company_name,
                            suffix = R.string.company_desc_suffix,
                            separator2 = '\n',
                        )
                        SurroundedLink(
                            text = text,
                            modifier = Modifier.padding(end = Theme.dimens.content_padding.end * 2),
                            style = centredTextStyle,
                        ) {
                            ExternalLink.openLink(AppConfig.get(COMPANY_WEBSITE))
                        }
                    }
                    VerticalSpacer()

                    CentredRow {
                        ContactUsLink()
                        ThemedLink(R.string.privacy_policy, R.drawable.baseline_security_24) {
                            ExternalLink.openLink(AppConfig.get(PRIVACY_POLICY_URL))
                        }
                        ThemedLink(R.string.source_code, R.drawable.baseline_code_24) {
                            ExternalLink.openLink(AppConfig.get(SOURCE_CODE_URL))
                        }
                    }
                    VerticalSpacer(1.5f)

                    Body("This software is published under", style = Theme.typography.labelLarge)
                    ThemedLink(
                        text = "The GNU General Public License (v3.0)",
                        R.drawable.baseline_open_in_browser_24
                    ) {
                        ExternalLink.openLink("https://www.gnu.org/licenses/gpl-3.0-standalone.html")
                    }
                    VerticalSpacer()
                }
                AboutCard(R.string.app_version_title) {
                    val config = AbstractApplication.INSTANCE.config
                    val buildDate = remember(config.buildEpoch) {
                        Localizer.from(Locale.current.language).formatDateTime(config.buildEpoch)
                    }

                    PaddedRow {
                        Body(R.string.build_time, fontWeight = FontWeight.Bold)
                        Body(buildDate)
                    }

                    PaddedRow {
                        Body(R.string.app_version, fontWeight = FontWeight.Bold)
                        Body(config.versionName)
                    }

                    PaddedRow {
                        Body(R.string.latest_app_version, fontWeight = FontWeight.Bold)
                        ThemedLink(
                            AppConfig.get(LATEST_VERSION_NAME),
                            R.drawable.baseline_cloud_download_24
                        ) {
                            ExternalLink.openLink(AppConfig.get(APP_DOWNLOAD_LINK))
                        }
                    }

                    PaddedRow {
                        val idLabel = stringRes(R.string.installation_id)
                        Body(idLabel, fontWeight = FontWeight.Bold)
                        val id = AppConfig.getState(INSTALLATION_ID).value
                        ThemedLink(id, R.drawable.baseline_content_copy_24) {
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(idLabel, id))
                            Toasts.short(R.string.id_copied_to_clipboard)
                        }
                    }
                    VerticalSpacer()
                }
            }
        }
    }
}

@Composable
private fun AboutCard(@StringRes titleRes: Int, content: @Composable ColumnScope.() -> Unit) {
    ContentCard(
        Modifier
            .padding(Theme.dimens.content_padding * 2)
            .sizeIn(
                minWidth = AppConfig.get(MIN_LAYOUT_COLUMN_WIDTH).dp,
                maxWidth = AppConfig.get(MAX_LAYOUT_COLUMN_WIDTH).dp
            )
    ) {
        PaddedColumn(Modifier.fillMaxSize()) {
            Title(titleRes)
            PaddedDivider()

            content()
        }
    }
}

@Composable
private fun VerticalSpacer(scale: Float = 1f) {
    val height = Theme.dimens.content_padding.top + Theme.dimens.content_padding.bottom
    Spacer(modifier = Modifier.height(height * scale))
}

@Composable
fun ContactUsLink() {
    ThemedLink(R.string.contact_us, R.drawable.baseline_email_24) {
        ExternalLink.openEmailDialer(AppConfig.get(CONTACT_EMAIL))
    }
}