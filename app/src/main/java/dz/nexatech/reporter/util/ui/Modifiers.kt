@file:Suppress("unused")

package dz.nexatech.reporter.util.ui

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp

@Stable
fun Modifier.contentPadding() = composed {
    padding(Theme.dimens.content_padding)
}

@Stable
fun Modifier.contentPadding(
    start: Dp = Dp.Unspecified,
    top: Dp = Dp.Unspecified,
    end: Dp = Dp.Unspecified,
    bottom: Dp = Dp.Unspecified,
) = composed {
    padding(Theme.dimens.content_padding.copy(start = start, top = top, end = end, bottom = bottom))
}

@Stable
fun Modifier.textPadding() = composed {
    padding(Theme.dimens.text_padding)
}

@Stable
fun Modifier.indentedTextPadding() = composed {
    padding(Theme.dimens.indented_text_padding)
}

@Stable
fun Modifier.textPadding(
    start: Dp = Dp.Unspecified,
    top: Dp = Dp.Unspecified,
    end: Dp = Dp.Unspecified,
    bottom: Dp = Dp.Unspecified,
) = composed {
    padding(Theme.dimens.text_padding.copy(start = start, top = top, end = end, bottom = bottom))
}

@Stable
fun Modifier.buttonPadding() = composed {
    padding(Theme.dimens.button_padding)
}

@Stable
fun Modifier.buttonPadding(
    start: Dp = Dp.Unspecified,
    top: Dp = Dp.Unspecified,
    end: Dp = Dp.Unspecified,
    bottom: Dp = Dp.Unspecified,
) = composed {
    padding(Theme.dimens.button_padding.copy(start = start, top = top, end = end, bottom = bottom))
}

@Stable
fun Modifier.minWidth(minWidth: Dp) =
    defaultMinSize(minWidth = minWidth, minHeight = Dp.Unspecified)

@Stable
fun Modifier.minHeight(minHeight: Dp) =
    defaultMinSize(minWidth = Dp.Unspecified, minHeight = minHeight)

@Stable
fun Modifier.roundedComponentTrailing() = composed {
    padding(end = Theme.dimens.rounded_component.start)
}

@Stable
fun Modifier.roundedComponentLeading() = composed {
    padding(start = Theme.dimens.rounded_component.end)
}

@Stable
fun Modifier.roundedComponentPadding() = composed {
    padding(Theme.dimens.rounded_component)
}