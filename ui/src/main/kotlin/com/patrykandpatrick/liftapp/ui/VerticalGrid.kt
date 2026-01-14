package com.patrykandpatrick.liftapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier,
    cells: GridCells = GridCells.Adaptive(dimens.grid.minCellWidthMedium),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit,
) {
    Layout(
        measurePolicy = { measurable, constraints ->
            val spacing = horizontalArrangement.spacing.roundToPx()
            val rowSizes =
                with(cells) {
                    calculateCrossAxisCellSizes(
                        availableSize = constraints.maxWidth,
                        spacing = spacing,
                    )
                }
            val placeables =
                measurable.chunked(rowSizes.size).map { measurables ->
                    var index = 0
                    val height = measurables.maxOf { it.minIntrinsicHeight(rowSizes[index++]) }
                    measurables.mapIndexed { index, measurable ->
                        val width = rowSizes[index % rowSizes.size]
                        measurable.measure(constraints.copy(width, width, height, height))
                    }
                }

            val placeablesHeights = placeables.map { columns -> columns.maxOf { it.height } }
            val height =
                placeablesHeights.sum() +
                    verticalArrangement.spacing.roundToPx() * (placeables.size - 1)

            layout(constraints.maxWidth, height) {
                val yPositions = IntArray(placeables.size)
                with(verticalArrangement) {
                    arrange(
                        totalSize = height,
                        sizes = placeablesHeights.toIntArray(),
                        outPositions = yPositions,
                    )
                }

                placeables.forEachIndexed { row, columns ->
                    val xPositions = IntArray(columns.size)
                    with(horizontalArrangement) {
                        arrange(
                            totalSize = constraints.maxWidth,
                            sizes = IntArray(xPositions.size) { rowSizes[it] },
                            layoutDirection = layoutDirection,
                            outPositions = xPositions,
                        )
                    }
                    columns.forEachIndexed { column, placeable ->
                        placeable.place(xPositions[column], yPositions[row])
                    }
                }
            }
        },
        content = content,
        modifier = modifier,
    )
}

@Composable
@Preview
private fun VerticalRowPreview() {
    LiftAppTheme {
        LiftAppBackground {
            VerticalGrid(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                repeat(6) { index ->
                    Box(
                        Modifier.background(colorScheme.primary)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        Alignment.Center,
                    ) {
                        LiftAppText(text = index.toString(), color = colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}
