@file:OptIn(ExperimentalLayoutApi::class)

package dz.nexatech.reporter.util.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCorner.ExtraLarge,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    content: @Composable ColumnScope.() -> Unit,
) = ElevatedCard(
    modifier,
    shape,
    colors,
    elevation,
    content
)

@Composable
inline fun CentredColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit
) = Column(
    modifier = modifier,
    verticalArrangement = verticalArrangement,
    horizontalAlignment = horizontalAlignment,
    content = content
)

@Composable
inline fun ScrollableColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    PaddedColumn(
        modifier = modifier
            .drawVerticalScrollbar(scrollState)
            .verticalScroll(scrollState)
            .fillMaxWidth(),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

@Composable
inline fun PaddedColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit
) = Column(
    modifier = modifier.contentPadding(),
    verticalArrangement = verticalArrangement,
    horizontalAlignment = horizontalAlignment,
    content = content
)

@Composable
inline fun PaddedBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    propagateMinConstraints: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) = Box(
    modifier = modifier.contentPadding(),
    contentAlignment = contentAlignment,
    propagateMinConstraints = propagateMinConstraints,
    content = content,
)

@Composable
fun CentredRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    content: @Composable RowScope.() -> Unit
) = FlowRow(
    modifier = modifier,
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment,
    maxItemsInEachRow = maxItemsInEachRow,
    content = content,
)

@Composable
fun PaddedRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    content: @Composable RowScope.() -> Unit
) = FlowRow(
    modifier = modifier.contentPadding(),
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment,
    maxItemsInEachRow = maxItemsInEachRow,
    content = content,
)

@Composable
fun PaddedDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
) {
    Divider(
        modifier = modifier
            .contentPadding(
                start = Theme.dimens.content_padding.start * 2,
                end = Theme.dimens.content_padding.end * 2,
            )
            .fillMaxWidth(),
        thickness = thickness,
        color = color,
    )
}

@Composable
fun SettingsDivider(modifier: Modifier = Modifier) = Divider(
    modifier = modifier
        .contentPadding(
            top = zero_padding,
            bottom = zero_padding,
            start = Theme.dimens.content_padding.start * 4,
            end = Theme.dimens.content_padding.end * 4,
        )
        .fillMaxWidth(),
)