package dz.nexatech.reporter.util.model

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dz.nexatech.reporter.client.model.MAX_LAYOUT_COLUMN_WIDTH
import dz.nexatech.reporter.client.model.MIN_LAYOUT_COLUMN_WIDTH
import dz.nexatech.reporter.util.ui.Dimens
import dz.nexatech.reporter.util.ui.Theme
import kotlin.math.min

@Composable
fun rememberDpState(intConfig: LocalConfig.Int): State<Dp> = remember {
    val dimen = AppConfig.intState(intConfig)
    derivedStateOf { dimen.value.dp }
}

@Composable
fun rememberColumnsCount(
    dimens: Dimens = Theme.dimens,
    config: Configuration = LocalConfiguration.current,
    screenWidthMapper: (Int) -> Int = { screenWidth ->
        (screenWidth - dimens.content_padding.horizontal.value * 2).toInt()
    },
): State<Int> =
    remember(config) {
        val screenWidth = screenWidthMapper(config.screenWidthDp)
        val columnWidth = AppConfig.intState(MIN_LAYOUT_COLUMN_WIDTH)
        derivedStateOf(structuralEqualityPolicy()) { screenWidth / columnWidth.value }
    }

@Composable
fun rememberLayoutWidth(
    columnsCount: State<Int>? = null, // null = one column
    config: Configuration = LocalConfiguration.current,
    dimens: Dimens = Theme.dimens,
): State<Dp> = remember(config, dimens) {
    val maxWidth = AppConfig.intState(MAX_LAYOUT_COLUMN_WIDTH)
    val screenWidth = config.screenWidthDp
    val horizontalPadding = dimens.content_padding.horizontal.value * 2
    if (columnsCount == null) {
        derivedStateOf {
            Dp(min(maxWidth.value, screenWidth) - horizontalPadding)
        }
    } else {
        derivedStateOf {
            Dp(min(maxWidth.value * columnsCount.value, screenWidth) - horizontalPadding)
        }
    }
}

fun MutableState<Boolean>.toggle(): MutableState<Boolean> {
    this.value = !value
    return this
}