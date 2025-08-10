package com.patrykandpatrick.liftapp.ui.modifier

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.layer.CompositingStrategy
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

fun Modifier.fadingEdges(
    startEdgeLength: Dp = 0.dp,
    topEdgeLength: Dp = 0.dp,
    endEdgeLength: Dp = 0.dp,
    bottomEdgeLength: Dp = 0.dp,
    startAlpha: Float = 1f,
    topAlpha: Float = 1f,
    endAlpha: Float = 1f,
    bottomAlpha: Float = 1f,
): Modifier {
    val paint = Paint()
    paint.blendMode = BlendMode.DstOut
    val colors = listOf(Color.Black, Color.Transparent)

    return drawWithContent {
        drawContent()
        drawContext.graphicsLayer?.compositingStrategy = CompositingStrategy.Offscreen

        if (startEdgeLength.value > 0) {
            Brush.horizontalGradient(colors)
                .applyTo(Size(startEdgeLength.toPx(), size.height), paint, startAlpha)
            drawContext.canvas.drawRect(
                left = 0f,
                top = 0f,
                right = startEdgeLength.toPx(),
                bottom = size.height,
                paint = paint,
            )
        }

        if (endEdgeLength.value > 0) {
            Brush.horizontalGradient(
                    colors,
                    startX = size.width,
                    endX = size.width - endEdgeLength.toPx(),
                )
                .applyTo(Size(endEdgeLength.toPx(), size.height), paint, endAlpha)
            drawContext.canvas.drawRect(
                left = size.width - endEdgeLength.toPx(),
                top = 0f,
                right = size.width,
                bottom = size.height,
                paint = paint,
            )
        }

        if (topEdgeLength.value > 0) {
            Brush.verticalGradient(colors)
                .applyTo(Size(size.width, topEdgeLength.toPx()), paint, topAlpha)
            drawContext.canvas.drawRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = topEdgeLength.toPx(),
                paint = paint,
            )
        }

        if (bottomEdgeLength.value > 0) {
            Brush.verticalGradient(
                    colors,
                    startY = size.height,
                    endY = size.height - bottomEdgeLength.toPx(),
                )
                .applyTo(Size(size.width, bottomEdgeLength.toPx()), paint, bottomAlpha)
            drawContext.canvas.drawRect(
                left = 0f,
                top = size.height - bottomEdgeLength.toPx(),
                right = size.width,
                bottom = size.height,
                paint = paint,
            )
        }
    }
}

fun Modifier.fadingEdges(horizontalEdgeLength: Dp = 0.dp, verticalEdgeLength: Dp = 0.dp): Modifier =
    fadingEdges(
        startEdgeLength = horizontalEdgeLength,
        topEdgeLength = verticalEdgeLength,
        endEdgeLength = horizontalEdgeLength,
        bottomEdgeLength = verticalEdgeLength,
    )

fun Modifier.fadingEdges(
    horizontalEdgeLength: Dp = 0.dp,
    verticalEdgeLength: Dp = 0.dp,
    lazyListState: LazyListState,
): Modifier {
    lazyListState.canScrollForward
    val isHorizontal = lazyListState.layoutInfo.orientation == Orientation.Horizontal
    return fadingEdges(
        startEdgeLength = horizontalEdgeLength,
        startAlpha = if (lazyListState.canScrollBackward && isHorizontal) 1f else 0f,
        topEdgeLength = verticalEdgeLength,
        topAlpha = if (lazyListState.canScrollBackward && !isHorizontal) 1f else 0f,
        endEdgeLength = horizontalEdgeLength,
        endAlpha = if (lazyListState.canScrollForward && isHorizontal) 1f else 0f,
        bottomEdgeLength = verticalEdgeLength,
        bottomAlpha = if (lazyListState.canScrollForward && !isHorizontal) 1f else 0f,
    )
}

@LightAndDarkThemePreview
@Composable
private fun VerticalFadingEdgesPreview() {
    LiftAppTheme {
        LiftAppBackground {
            Column(modifier = Modifier.fadingEdges(verticalEdgeLength = 32.dp)) {
                repeat(5) { Text(text = "Text") }
            }
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun HorizontalFadingEdgesPreview() {
    LiftAppTheme {
        LiftAppBackground {
            Row(modifier = Modifier.fadingEdges(horizontalEdgeLength = 32.dp)) {
                repeat(5) { Text(text = "Text") }
            }
        }
    }
}
