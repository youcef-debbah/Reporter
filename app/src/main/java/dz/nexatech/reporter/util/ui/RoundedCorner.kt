package dz.nexatech.reporter.util.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

internal object RoundedCorner {
    val ExtraLarge = RoundedCornerShape(28.0.dp)
    val ExtraLargeTop =
        RoundedCornerShape(
            topStart = 28.0.dp,
            topEnd = 28.0.dp,
            bottomEnd = 0.0.dp,
            bottomStart = 0.0.dp
        )
    val ExtraLargeBottom =
        RoundedCornerShape(
            topStart = 0.0.dp,
            topEnd = 0.0.dp,
            bottomEnd = 28.0.dp,
            bottomStart = 28.0.dp
        )
    val ExtraSmall = RoundedCornerShape(4.0.dp)
    val ExtraSmallTop = RoundedCornerShape(
        topStart = 4.0.dp,
        topEnd = 4.0.dp,
        bottomEnd = 0.0.dp,
        bottomStart = 0.0.dp
    )
    val Full = CircleShape
    val Large = RoundedCornerShape(16.0.dp)
    val LargeEnd =
        RoundedCornerShape(
            topStart = 0.0.dp,
            topEnd = 16.0.dp,
            bottomEnd = 16.0.dp,
            bottomStart = 0.0.dp
        )
    val LargeTop =
        RoundedCornerShape(
            topStart = 16.0.dp,
            topEnd = 16.0.dp,
            bottomEnd = 0.0.dp,
            bottomStart = 0.0.dp
        )
    val Medium = RoundedCornerShape(12.0.dp)
    val None = RectangleShape
    val Small = RoundedCornerShape(8.0.dp)
}