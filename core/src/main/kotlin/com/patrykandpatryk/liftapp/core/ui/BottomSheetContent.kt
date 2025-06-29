package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun BottomSheetContent(
    topContent: @Composable BoxScope.() -> Unit,
    bottomContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    minHeightFactor: Float = 0.33f,
) {
    Layout(
        content = {
            Box(content = topContent)
            Box(content = bottomContent)
        },
        modifier = modifier,
    ) { measureables, constraints ->
        val topPlaceable = measureables[0].measure(constraints)
        val bottomPlaceable =
            measureables[1].measure(
                constraints.copy(
                    minHeight = 0,
                    maxHeight = constraints.maxHeight - topPlaceable.height,
                )
            )

        val height =
            (constraints.maxHeight * minHeightFactor)
                .roundToInt()
                .coerceAtLeast(topPlaceable.height + bottomPlaceable.height)
        val width = constraints.maxWidth

        layout(width, height) {
            topPlaceable.place(0, 0)
            bottomPlaceable.place(0, height - bottomPlaceable.height)
        }
    }
}

@Preview
@Composable
private fun SmallBottomSheetContentPreview() {
    BottomSheetContent(
        topContent = { Box(Modifier.fillMaxWidth().height(4.dp).background(Color.Red)) },
        bottomContent = { Box(Modifier.fillMaxWidth().height(4.dp).background(Color.Red)) },
        modifier = Modifier,
        minHeightFactor = 0.4f,
    )
}
