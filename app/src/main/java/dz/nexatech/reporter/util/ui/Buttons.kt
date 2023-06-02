package dz.nexatech.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ThemedLink(
    text: String,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
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