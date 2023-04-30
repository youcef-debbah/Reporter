package dz.nexatech.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ThemedLink(
    text: String,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    @StringRes desc: Int
) {
    TextButton(
        modifier = Modifier.height(Theme.dimens.link_button_height),
        contentPadding = Theme.dimens.rounded_component,
        onClick = onClick
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = stringRes(desc),
        )
        ThemedText(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}