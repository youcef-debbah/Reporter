package dz.nexatech.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.FilesExtension
import dz.nexatech.reporter.client.common.MimeType
import dz.nexatech.reporter.client.common.putValue
import dz.nexatech.reporter.client.model.AssetResource
import dz.nexatech.reporter.client.model.RESOURCE_PREFIX

const val ICON_RESOURCE_PREFIX = RESOURCE_PREFIX + "icons/"
const val ICON_RESOURCE_EXTENSION = FilesExtension.SVG
const val ICON_RESOURCE_MIME_TYPE = MimeType.SVG

@Stable
interface AbstractIcon {
    @Composable
    fun painterResource(): Painter
}

@Stable
class StaticIcon private constructor(@DrawableRes val drawable: Int): AbstractIcon {
    @Composable
    override fun painterResource(): Painter =
        painterResource(drawable)

    companion object {
        val baseline_settings = StaticIcon(R.drawable.baseline_settings_24)
        val baseline_downloading = StaticIcon(R.drawable.baseline_downloading_24)
        val baseline_warning = StaticIcon(R.drawable.baseline_warning_24)
        val baseline_preview = StaticIcon(R.drawable.baseline_preview_24)
        val baseline_table_rows = StaticIcon(R.drawable.baseline_table_rows_24)
        val baseline_keyboard = StaticIcon(R.drawable.baseline_keyboard_24)
        val baseline_home = StaticIcon(R.drawable.baseline_home_24)
        val baseline_upload_file = StaticIcon(R.drawable.baseline_upload_file_24)
        val baseline_open_in_browser = StaticIcon(R.drawable.baseline_open_in_browser_24)
        val baseline_event = StaticIcon(R.drawable.baseline_event_24)
        val baseline_color_lens = StaticIcon(R.drawable.baseline_color_lens_24)
        val baseline_list = StaticIcon(R.drawable.baseline_list_24)
        val baseline_dialpad = StaticIcon(R.drawable.baseline_dialpad_24)
        val baseline_email = StaticIcon(R.drawable.baseline_email_24)
        val baseline_call = StaticIcon(R.drawable.baseline_call_24)
        val baseline_language = StaticIcon(R.drawable.baseline_language_24)
        val baseline_exposure = StaticIcon(R.drawable.baseline_exposure_24)
        val baseline_notes = StaticIcon(R.drawable.baseline_notes_24)
        val baseline_delete_forever = StaticIcon(R.drawable.baseline_delete_forever_24)
    }
}

fun iconPath(name: String) = "$ICON_RESOURCE_PREFIX$name.$ICON_RESOURCE_EXTENSION"

@Stable
abstract class AbstractPrintableIconResource(
    val name: String,
) : AssetResource(iconPath(name), ICON_RESOURCE_MIME_TYPE), AbstractIcon

@Stable
private class StaticPrintableIconResource(
    name: String,
    @DrawableRes val drawable: Int
) : AbstractPrintableIconResource(name) {
    @Composable
    override fun painterResource(): Painter =
        painterResource(drawable)
}

val iconsAssetsResources: ImmutableMap<String, AbstractPrintableIconResource> = ImmutableMap.Builder<String, AbstractPrintableIconResource>()
    .putValue(StaticPrintableIconResource("baseline_build_24", R.drawable.baseline_build_24))
    .putValue(StaticPrintableIconResource("baseline_call_24", R.drawable.baseline_call_24))
    .putValue(StaticPrintableIconResource("baseline_close_24", R.drawable.baseline_close_24))
    .putValue(StaticPrintableIconResource("baseline_color_lens_24", R.drawable.baseline_color_lens_24))
    .putValue(StaticPrintableIconResource("baseline_comment_24", R.drawable.baseline_comment_24))
    .putValue(
        StaticPrintableIconResource(
            "baseline_contact_emergency_24",
            R.drawable.baseline_contact_emergency_24
        )
    )
    .putValue(StaticPrintableIconResource("baseline_contact_mail_24", R.drawable.baseline_contact_mail_24))
    .putValue(
        StaticPrintableIconResource(
            "baseline_contact_phone_24",
            R.drawable.baseline_contact_phone_24
        )
    )
    .putValue(
        StaticPrintableIconResource(
            "baseline_corporate_fare_24",
            R.drawable.baseline_corporate_fare_24
        )
    )
    .putValue(StaticPrintableIconResource("baseline_credit_card_24", R.drawable.baseline_credit_card_24))
    .putValue(StaticPrintableIconResource("baseline_dialpad_24", R.drawable.baseline_dialpad_24))
    .putValue(StaticPrintableIconResource("baseline_discount_24", R.drawable.baseline_discount_24))
    .putValue(
        StaticPrintableIconResource(
            "baseline_display_settings_24",
            R.drawable.baseline_display_settings_24
        )
    )
    .putValue(StaticPrintableIconResource("baseline_done_24", R.drawable.baseline_done_24))
    .putValue(StaticPrintableIconResource("baseline_edit_24", R.drawable.baseline_edit_24))
    .putValue(StaticPrintableIconResource("baseline_email_24", R.drawable.baseline_email_24))
    .putValue(StaticPrintableIconResource("baseline_emergency_24", R.drawable.baseline_emergency_24))
    .putValue(StaticPrintableIconResource("baseline_euro_24", R.drawable.baseline_euro_24))
    .putValue(StaticPrintableIconResource("baseline_event_24", R.drawable.baseline_event_24))
    .putValue(
        StaticPrintableIconResource(
            "baseline_event_available_24",
            R.drawable.baseline_event_available_24
        )
    )
    .putValue(StaticPrintableIconResource("baseline_event_busy_24", R.drawable.baseline_event_busy_24))
    .putValue(StaticPrintableIconResource("baseline_exposure_24", R.drawable.baseline_exposure_24))
    .putValue(StaticPrintableIconResource("baseline_grade_24", R.drawable.baseline_grade_24))
    .putValue(StaticPrintableIconResource("baseline_home_24", R.drawable.baseline_home_24))
    .putValue(StaticPrintableIconResource("baseline_info_24", R.drawable.baseline_info_24))
    .putValue(StaticPrintableIconResource("baseline_input_24", R.drawable.baseline_input_24))
    .putValue(StaticPrintableIconResource("baseline_keyboard_24", R.drawable.baseline_keyboard_24))
    .putValue(StaticPrintableIconResource("baseline_list_24", R.drawable.baseline_list_24))
    .putValue(StaticPrintableIconResource("baseline_percent_24", R.drawable.baseline_percent_24))
    .putValue(StaticPrintableIconResource("baseline_place_24", R.drawable.baseline_place_24))
    .putValue(StaticPrintableIconResource("baseline_text_fields_24", R.drawable.baseline_text_fields_24))
    .putValue(StaticPrintableIconResource("ms_height_24", R.drawable.ms_height_24))
    .putValue(StaticPrintableIconResource("ms_width_24", R.drawable.ms_width_24))
    .build()