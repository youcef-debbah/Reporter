package com.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

@Composable
fun VectorIcon(
    @DrawableRes icon: Int,
    @StringRes desc: Int?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Icon(
        imageVector = ImageVector.vectorResource(icon),
        contentDescription = desc?.let { stringRes(it) },
        modifier = modifier,
        tint = tint,
    )
}