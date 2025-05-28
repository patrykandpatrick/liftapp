package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.graphics.addSinLine
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlin.math.roundToInt

private fun Modifier.sinDivider(
    color: Color,
    thickness: Dp,
    sinPeriodLength: Dp,
    horizontalExtent: Dp,
) =
    then(
        Modifier.drawWithCache {
            val path = Path()
            onDrawBehind {
                val strokeWidth = thickness.roundToPx()
                path.addSinLine(
                    start = -strokeWidth / 2 - horizontalExtent.roundToPx(),
                    end = size.width.roundToInt() + strokeWidth / 2 + horizontalExtent.roundToPx(),
                    sinPeriodLength = sinPeriodLength.roundToPx(),
                    sinHeight = size.height.roundToInt() - thickness.roundToPx(),
                )
                path.translate(Offset(0f, strokeWidth / 2f))
                drawPath(path, color, style = Stroke(strokeWidth.toFloat()))
                path.rewind()
            }
        }
    )

@Composable
fun SinHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = colorScheme.outline,
    thickness: Dp = dimens.strokeWidth,
    sinHeight: Dp = dimens.divider.sinHeight,
    sinPeriodLength: Dp = dimens.divider.sinPeriodLength,
    horizontalExtent: Dp = 0.dp,
) {
    Spacer(
        modifier =
            modifier
                .height(sinHeight)
                .fillMaxWidth()
                .sinDivider(
                    color = color,
                    thickness = thickness,
                    sinPeriodLength = sinPeriodLength,
                    horizontalExtent = horizontalExtent,
                )
    )
}

@LightAndDarkThemePreview
@Composable
fun SinHorizontalDividerPreview() {
    LiftAppTheme { LiftAppBackground { SinHorizontalDivider() } }
}
