package dz.nexatech.reporter.util.model

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dz.nexatech.reporter.client.model.MAX_LAYOUT_COLUMN_WIDTH
import dz.nexatech.reporter.client.model.MIN_LAYOUT_COLUMN_WIDTH
import dz.nexatech.reporter.util.ui.Theme
import kotlin.math.min

@Composable
fun rememberDpState(intConfig: LocalConfig.Int): State<Dp> = remember {
    val dimen by AppConfig.intState(intConfig)
    derivedStateOf { dimen.dp }
}

@Composable
fun rememberColumnsCount(config: Configuration = LocalConfiguration.current): State<Int> {
    return remember(config) {
        val screenWidth = config.screenWidthDp
        val columnWidth by AppConfig.intState(MIN_LAYOUT_COLUMN_WIDTH)
        derivedStateOf(structuralEqualityPolicy()) { screenWidth / columnWidth }
    }
}

@Composable
fun rememberMaxLayoutColumnWidth(): State<Dp> {
    val config = LocalConfiguration.current
    val dimens = Theme.dimens
    return remember(config, dimens) {
        val maxWidth by AppConfig.intState(MAX_LAYOUT_COLUMN_WIDTH)
        val screenWidth = config.screenWidthDp
        val horizontalPadding = dimens.content_padding.horizontal.value * 2
        derivedStateOf {
            Dp(min(maxWidth, screenWidth) - horizontalPadding)
        }
    }
}