package dz.nexatech.reporter.util.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Stable
private val padding_unit = 3.dp

@Stable
val zero_padding = 0.dp

@Stable
val small_padding = padding_unit

@Stable
val medium_padding = padding_unit * 2

@Stable
val large_padding = padding_unit * 3

@Stable
val huge_padding = padding_unit * 4

@Immutable
data class Dimens(
    @Stable
    val text_padding: StaticPadding,
    @Stable
    val button_padding: StaticPadding,
    @Stable
    val content_padding: StaticPadding,
    @Stable
    val rounded_component: StaticPadding,
    @Stable
    val drop_menu_min_width: Dp,
    @Stable
    val link_button_height: Dp,
)

val LocalDimens = staticCompositionLocalOf {
    Dimens(
        text_padding = StaticPadding(
            small_padding,
            zero_padding,
            small_padding,
            zero_padding
        ),
        button_padding = StaticPadding(
            medium_padding,
            small_padding,
            medium_padding,
            small_padding
        ),
        content_padding = StaticPadding(
            medium_padding,
            medium_padding,
            medium_padding,
            medium_padding
        ),
        rounded_component = StaticPadding(
            huge_padding,
            zero_padding,
            huge_padding,
            zero_padding
        ),
        drop_menu_min_width = 150.dp,
        link_button_height = 30.dp,
    )
}

@Composable
fun UpdateDimens(updater: (Dimens) -> Dimens, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalDimens provides updater.invoke(LocalDimens.current),
        content = content
    )
}

/**
 * Describes an absolute (RTL aware) padding to be applied along the edges inside a box.
 */
@Immutable
class StaticPadding(
    @Stable
    val start: Dp = 0.dp,
    @Stable
    val top: Dp = 0.dp,
    @Stable
    val end: Dp = 0.dp,
    @Stable
    val bottom: Dp = 0.dp
) : PaddingValues {

    fun copy(
        start: Dp = Dp.Unspecified,
        top: Dp = Dp.Unspecified,
        end: Dp = Dp.Unspecified,
        bottom: Dp = Dp.Unspecified,
    ): StaticPadding = StaticPadding(
        if (start == Dp.Unspecified || start == Dp.Infinity) this.start else start,
        if (top == Dp.Unspecified || start == Dp.Infinity) this.top else top,
        if (end == Dp.Unspecified || start == Dp.Infinity) this.end else end,
        if (bottom == Dp.Unspecified || start == Dp.Infinity) this.bottom else bottom,
    )

    override fun calculateLeftPadding(layoutDirection: LayoutDirection) =
        if (layoutDirection == LayoutDirection.Rtl) end else start

    override fun calculateRightPadding(layoutDirection: LayoutDirection) =
        if (layoutDirection == LayoutDirection.Rtl) start else end

    override fun calculateTopPadding() = top

    override fun calculateBottomPadding() = bottom

    override fun equals(other: Any?): Boolean {
        if (other !is StaticPadding) return false
        return start == other.start &&
                top == other.top &&
                end == other.end &&
                bottom == other.bottom
    }

    override fun hashCode() =
        ((start.hashCode() * 31 + top.hashCode()) * 31 + end.hashCode()) *
                31 + bottom.hashCode()

    override fun toString() =
        "PaddingValues.Absolute(start=$start, top=$top, end=$end, bottom=$bottom)"

    @Stable
    operator fun plus(value: Dp) = StaticPadding(
        start = start.plus(value),
        top = top.plus(value),
        end = end.plus(value),
        bottom = bottom.plus(value),
    )

    @Stable
    operator fun minus(value: Dp) = StaticPadding(
        start = start.minus(value),
        top = top.minus(value),
        end = end.minus(value),
        bottom = bottom.minus(value),
    )

    @Stable
    operator fun unaryMinus() = StaticPadding(
        start = start.unaryMinus(),
        top = top.unaryMinus(),
        end = end.unaryMinus(),
        bottom = bottom.unaryMinus(),
    )

    @Stable
    operator fun div(value: Int): StaticPadding =
        StaticPadding(
            start = start.div(value),
            top = top.div(value),
            end = end.div(value),
            bottom = bottom.div(value),
        )

    @Stable
    operator fun times(value: Int): StaticPadding =
        StaticPadding(
            start = start.times(value),
            top = top.times(value),
            end = end.times(value),
            bottom = bottom.times(value),
        )
}
