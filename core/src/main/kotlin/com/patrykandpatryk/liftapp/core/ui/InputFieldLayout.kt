package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.extension.horizontal
import com.patrykandpatryk.liftapp.core.extension.vertical

private const val InputFieldAnimationDuration = 150

@Composable
fun InputFieldLayout(
    modifier: Modifier = Modifier,
    cornerSize: CornerSize = CornerSize(8.dp),
    strokeWidth: Dp = 1.dp,
    labelStartMargin: Dp = LocalDimens.current.input.labelStartMargin,
    labelHorizontalPadding: Dp = LocalDimens.current.input.labelHorizontalPadding,
    contentPadding: PaddingValues =
        PaddingValues(
            horizontal = LocalDimens.current.padding.itemHorizontal,
            vertical = LocalDimens.current.padding.itemVerticalSmall,
        ),
    label: (@Composable RowScope.() -> Unit)? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    content: @Composable RowScope.() -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    var labelWidth by remember { mutableIntStateOf(0) }
    var labelHeight by remember { mutableIntStateOf(0) }
    val strokeColor by
        animateColorAsState(
            targetValue =
                when {
                    isError -> colors.errorIndicatorColor
                    !enabled -> colors.disabledIndicatorColor
                    else -> colors.unfocusedIndicatorColor
                },
            label = "stroke color",
            animationSpec = tween(InputFieldAnimationDuration),
        )
    val labelColor by
        animateColorAsState(
            targetValue =
                when {
                    isError -> colors.errorLabelColor
                    !enabled -> colors.disabledLabelColor
                    else -> colors.unfocusedLabelColor
                },
            label = "label color",
            animationSpec = tween(InputFieldAnimationDuration),
        )
    val path = remember { Path() }

    Layout(
        content = {
            Row(verticalAlignment = Alignment.CenterVertically, content = content)
            Row(verticalAlignment = Alignment.CenterVertically) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodySmall,
                    LocalContentColor provides labelColor,
                ) {
                    label?.invoke(this)
                }
            }
        },
        measurePolicy =
            MeasurePolicy { measurables, constraints ->
                val maxWidth = constraints.maxWidth - contentPadding.horizontal().roundToPx()
                val coercedConstraints =
                    constraints.copy(
                        minWidth =
                            (constraints.minWidth + contentPadding.horizontal().roundToPx())
                                .coerceAtMost(maxWidth),
                        maxWidth = maxWidth,
                    )
                val contentPlaceable = measurables[0].measure(coercedConstraints)
                val labelPlaceable =
                    measurables.getOrNull(1)?.measure(constraints.copy(minWidth = 0))
                labelWidth = labelPlaceable?.width ?: 0
                labelHeight = labelPlaceable?.height ?: 0

                layout(
                    width =
                        (labelWidth +
                                labelHorizontalPadding.roundToPx() +
                                labelStartMargin.roundToPx())
                            .coerceAtLeast(contentPlaceable.width)
                            .coerceAtMost(maxWidth) + contentPadding.horizontal().roundToPx(),
                    height =
                        contentPlaceable.height +
                            labelHeight * 2 +
                            contentPadding.vertical().roundToPx(),
                ) {
                    contentPlaceable.placeRelative(
                        x = contentPadding.calculateStartPadding(layoutDirection).roundToPx(),
                        y = labelHeight + contentPadding.calculateTopPadding().roundToPx(),
                    )

                    labelPlaceable?.apply {
                        placeRelative((labelStartMargin + labelHorizontalPadding).roundToPx(), 0)
                    }
                }
            },
        modifier =
            modifier.drawWithCache {
                onDrawBehind {
                    path.rewind()
                    val cornerSize =
                        cornerSize.toPx(
                            shapeSize = Size(size.width, size.height - labelHeight),
                            density = this,
                        )
                    var x = labelStartMargin.toPx() + labelHorizontalPadding.toPx() * 2 + labelWidth
                    var y = 0f
                    path.moveTo(x, y)
                    x = size.width - strokeWidth.toPx() / 2 - cornerSize
                    path.lineTo(x, y)
                    y += cornerSize
                    path.addCorner(x, y, cornerSize, 270f)
                    x += cornerSize
                    y = size.height - labelHeight - cornerSize
                    path.lineTo(x, y)
                    x -= cornerSize
                    path.addCorner(x, y, cornerSize, 0f)
                    x = strokeWidth.toPx() / 2 + cornerSize
                    y += cornerSize
                    path.lineTo(x, y)
                    y -= cornerSize
                    path.addCorner(x, y, cornerSize, 90f)
                    x = strokeWidth.toPx() / 2
                    y = cornerSize
                    path.lineTo(x, y)
                    x += cornerSize
                    path.addCorner(x, y, cornerSize, 180f)
                    path.lineTo(labelStartMargin.toPx(), 0f)
                    path.translate(Offset(0f, labelHeight / 2f))
                    drawPath(
                        path = path,
                        color = strokeColor,
                        style = Stroke(width = strokeWidth.toPx()),
                    )
                }
            },
    )
}

private fun Path.addCorner(x: Float, y: Float, cornerSize: Float, startAngle: Float) {
    addArc(
        oval = Rect(x - cornerSize, y - cornerSize, x + cornerSize, y + cornerSize),
        startAngleDegrees = startAngle,
        sweepAngleDegrees = 90f,
    )
}

@LightAndDarkThemePreview
@Composable
private fun InputFieldLayoutPreview() {
    LiftAppTheme {
        Surface {
            InputFieldLayout(
                label = { Text(text = "Label") },
                content = {
                    Text(
                        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
            )
        }
    }
}
