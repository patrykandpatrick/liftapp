package com.patrykandpatrick.liftapp.ui.graphics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import kotlin.math.roundToInt

class TopSinShape(
    private val sinPeriodLength: Dp,
    private val sinHeight: Dp,
    private val strokeWidth: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline =
        with(density) {
            val strokeWidth = strokeWidth.roundToPx()
            val path =
                Path().apply {
                    addSinLine(
                        start = -strokeWidth / 2,
                        end = size.width.roundToInt() + strokeWidth / 2,
                        sinPeriodLength = sinPeriodLength.roundToPx(),
                        sinHeight = sinHeight.roundToPx(),
                    )
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
            Outline.Generic(path)
        }
}

@Composable
fun rememberTopSinShape(
    sinPeriodLength: Dp = LocalDimens.current.divider.sinPeriodLength,
    sinHeight: Dp = LocalDimens.current.divider.sinHeight,
    strokeWidth: Dp = LocalDimens.current.strokeWidth,
): TopSinShape =
    remember(sinPeriodLength, sinHeight, strokeWidth) {
        TopSinShape(sinPeriodLength, sinHeight, strokeWidth)
    }
