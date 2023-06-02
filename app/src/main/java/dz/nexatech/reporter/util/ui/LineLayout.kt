package dz.nexatech.reporter.util.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.DEFAULT_LINE_ELEMENT_WIDTH_LIMIT
import kotlin.math.min

@Preview
@Composable
fun LinePreview() {
    Line {
        Spacer(modifier = Modifier
            .background(Color.Red)
            .size(90.dp, 90.dp))
        Spacer(modifier = Modifier
            .background(Color.Green)
            .size(120.dp, 60.dp))
        Spacer(modifier = Modifier
            .background(Color.Blue)
            .size(60.dp, 120.dp))
    }
}

@Composable
fun Line(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        measurePolicy = LineMeasurePolicy,
        content = content,
    )
}

//object LineScope {
//    override fun Modifier.align(alignment: Alignment.Vertical): Modifier {
//    }
//
//    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier {
//    }
//
//    override fun Modifier.alignBy(alignmentLine: HorizontalAlignmentLine): Modifier {
//    }
//
//    override fun Modifier.alignByBaseline(): Modifier {
//    }
//
//    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier {
//    }
//}

object LineMeasurePolicy : MeasurePolicy {

    val defaultChildWidthLimit = AppConfig.get(DEFAULT_LINE_ELEMENT_WIDTH_LIMIT).dp.toPixels()

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val maxWidth = constraints.maxWidth
        val childrenCount = measurables.size
        val maxChildWidth = min(maxWidth / childrenCount, defaultChildWidthLimit)

        val childMaxWidthConstraint = min(constraints.maxWidth, maxChildWidth)
        val childMinWidthConstraint = min(constraints.minWidth, childMaxWidthConstraint)
        val lineConstraints = constraints.copy(
            maxWidth = childMaxWidthConstraint,
            minWidth = childMinWidthConstraint,
        )

        val placeables = ArrayList<Placeable>(childrenCount)
        var maxHeight = 0
        var totalWidth = 0
        for (measurable in measurables) {
            val placeable = measurable.measure(lineConstraints)
            totalWidth += placeable.width
            val height = placeable.height
            if (height > maxHeight)
                maxHeight = height
            placeables.add(placeable)
        }

        val interSpace = (maxWidth - totalWidth) / (childrenCount + 1)

        return layout(maxWidth, maxHeight) {
            var position = interSpace
            for (placeable in placeables) {
                val height = placeable.height
                placeable.placeRelative(position, maxHeight - height)
                position += placeable.width + interSpace
            }
        }
    }
}
