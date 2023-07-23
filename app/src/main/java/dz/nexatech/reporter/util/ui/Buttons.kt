package dz.nexatech.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dz.nexatech.reporter.client.R

@Composable
fun ThemedLink(
    textRes: Int,
    @DrawableRes icon: Int = R.drawable.baseline_open_in_browser_24,
    onClick: () -> Unit,
) {
    ThemedLink(
        text = stringRes(textRes),
        icon = icon,
        onClick = onClick,
    )
}

@Composable
fun ThemedLink(
    text: String,
    @DrawableRes icon: Int = R.drawable.baseline_open_in_browser_24,
    onClick: () -> Unit,
) {
    TextButton(
        modifier = Modifier.height(Theme.dimens.link_button_height),
        contentPadding = Theme.dimens.rounded_component,
        onClick = onClick
    ) {
        DecorativeIcon(icon = icon)
        Body(
            text = text,
            style = Theme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}