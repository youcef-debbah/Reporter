package dz.nexatech.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.FilesExtension
import dz.nexatech.reporter.client.common.MimeType
import dz.nexatech.reporter.client.common.putValue
import dz.nexatech.reporter.client.model.AssetResource
import dz.nexatech.reporter.client.model.ResourcesRepository

const val ICON_RESOURCE_PREFIX = "icons/"
const val ICON_RESOURCE_EXTENSION = FilesExtension.SVG
const val ICON_RESOURCE_MIME_TYPE = MimeType.SVG

abstract class AbstractIconResource(
    val name: String,
) : AssetResource("$ICON_RESOURCE_PREFIX$name.$ICON_RESOURCE_EXTENSION", ICON_RESOURCE_MIME_TYPE) {
    @Composable
    abstract fun painterResource(resourcesRepository: ResourcesRepository): Painter

    final override fun toString() = path
}

private class DrawableIconResource(
    name: String,
    @DrawableRes val drawable: Int
) : AbstractIconResource(name) {
    @Composable
    override fun painterResource(resourcesRepository: ResourcesRepository): Painter =
        painterResource(drawable)
}

val iconsAssetsResources = ImmutableMap.Builder<String, AbstractIconResource>()
    .putValue(DrawableIconResource("baseline_build_24", R.drawable.baseline_build_24))
    .putValue(DrawableIconResource("baseline_call_24", R.drawable.baseline_call_24))
    .putValue(DrawableIconResource("baseline_close_24", R.drawable.baseline_close_24))
    .putValue(DrawableIconResource("baseline_color_lens_24", R.drawable.baseline_color_lens_24))
    .putValue(DrawableIconResource("baseline_comment_24", R.drawable.baseline_comment_24))
    .putValue(
        DrawableIconResource(
            "baseline_contact_emergency_24",
            R.drawable.baseline_contact_emergency_24
        )
    )
    .putValue(DrawableIconResource("baseline_contact_mail_24", R.drawable.baseline_contact_mail_24))
    .putValue(
        DrawableIconResource(
            "baseline_contact_phone_24",
            R.drawable.baseline_contact_phone_24
        )
    )
    .putValue(
        DrawableIconResource(
            "baseline_corporate_fare_24",
            R.drawable.baseline_corporate_fare_24
        )
    )
    .putValue(DrawableIconResource("baseline_credit_card_24", R.drawable.baseline_credit_card_24))
    .putValue(DrawableIconResource("baseline_dialpad_24", R.drawable.baseline_dialpad_24))
    .putValue(DrawableIconResource("baseline_discount_24", R.drawable.baseline_discount_24))
    .putValue(
        DrawableIconResource(
            "baseline_display_settings_24",
            R.drawable.baseline_display_settings_24
        )
    )
    .putValue(DrawableIconResource("baseline_done_24", R.drawable.baseline_done_24))
    .putValue(DrawableIconResource("baseline_edit_24", R.drawable.baseline_edit_24))
    .putValue(DrawableIconResource("baseline_email_24", R.drawable.baseline_email_24))
    .putValue(DrawableIconResource("baseline_emergency_24", R.drawable.baseline_emergency_24))
    .putValue(DrawableIconResource("baseline_euro_24", R.drawable.baseline_euro_24))
    .putValue(DrawableIconResource("baseline_event_24", R.drawable.baseline_event_24))
    .putValue(
        DrawableIconResource(
            "baseline_event_available_24",
            R.drawable.baseline_event_available_24
        )
    )
    .putValue(DrawableIconResource("baseline_event_busy_24", R.drawable.baseline_event_busy_24))
    .putValue(DrawableIconResource("baseline_exposure_24", R.drawable.baseline_exposure_24))
    .putValue(DrawableIconResource("baseline_grade_24", R.drawable.baseline_grade_24))
    .putValue(DrawableIconResource("baseline_home_24", R.drawable.baseline_home_24))
    .putValue(DrawableIconResource("baseline_info_24", R.drawable.baseline_info_24))
    .putValue(DrawableIconResource("baseline_input_24", R.drawable.baseline_input_24))
    .putValue(DrawableIconResource("baseline_keyboard_24", R.drawable.baseline_keyboard_24))
    .putValue(DrawableIconResource("baseline_list_24", R.drawable.baseline_list_24))
    .putValue(DrawableIconResource("baseline_percent_24", R.drawable.baseline_percent_24))
    .putValue(DrawableIconResource("baseline_place_24", R.drawable.baseline_place_24))
    .putValue(DrawableIconResource("baseline_text_fields_24", R.drawable.baseline_text_fields_24))
    .putValue(DrawableIconResource("ms_height_24", R.drawable.ms_height_24))
    .putValue(DrawableIconResource("ms_width_24", R.drawable.ms_width_24))
    .build()