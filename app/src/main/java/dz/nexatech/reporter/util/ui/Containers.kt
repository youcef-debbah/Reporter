package dz.nexatech.reporter.util.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCorner.ExtraLarge,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    content: @Composable ColumnScope.() -> Unit,
) = ElevatedCard(
    modifier
        .padding(Theme.dimens.content_padding * 2)
        .fillMaxWidth(),
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
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = verticalArrangement,
    horizontalAlignment = horizontalAlignment,
    content = content
)

@Composable
inline fun PaddedColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit
) = Column(
    modifier = modifier
        .fillMaxWidth()
        .contentPadding(),
    verticalArrangement = verticalArrangement,
    horizontalAlignment = horizontalAlignment,
    content = content
)

@Composable
inline fun CentredRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) = Row(
    modifier = modifier,
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment,
    content = content
)

@Composable
inline fun PaddedRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) = Row(
    modifier = modifier.contentPadding(),
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment,
    content = content
)