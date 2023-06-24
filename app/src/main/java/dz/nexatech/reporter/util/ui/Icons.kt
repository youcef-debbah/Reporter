package dz.nexatech.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

const val DisabledIconOpacity = 0.38f

@Composable
fun DecorativeIcon(
    @DrawableRes icon: Int?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    InfoIcon(icon = icon, desc = null, modifier, tint)
}

@Composable
fun DecorativeIcon(
    icon: AbstractIcon?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    InfoIcon(icon = icon, desc = null, modifier = modifier, tint = tint)
}

@Composable
fun InfoIcon(
    @DrawableRes icon: Int?,
    desc: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    if (icon != null) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = desc,
            modifier = modifier,
            tint = tint,
        )
    }
}

@Composable
fun InfoIcon(
    icon: AbstractIcon?,
    desc: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    if (icon != null) {
        Icon(
            painter = icon.painterResource(),
            contentDescription = desc,
            modifier = modifier,
            tint = tint,
        )
    }
}

@Composable
fun InfoIcon(
    @DrawableRes icon: Int?,
    @StringRes desc: Int,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    InfoIcon(
        icon = icon,
        desc = stringRes(desc),
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun InfoIcon(
    icon: AbstractIcon?,
    @StringRes desc: Int,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    InfoIcon(
        icon = icon,
        desc = stringRes(desc),
        modifier = modifier,
        tint = tint,
    )
}