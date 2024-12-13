package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.patrykandpatryk.liftapp.core.graphics.addSinLine
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import kotlin.math.roundToInt

private fun Modifier.sinDivider(color: Color) =
    then(
        Modifier.composed {
            val dimens = LocalDimens.current
            val separator = dimens.divider
            val path = remember { Path() }

            drawWithCache {
                onDrawBehind {
                    val strokeWidth = dimens.strokeWidth.roundToPx()
                    path.addSinLine(
                        start = -strokeWidth / 2,
                        end = size.width.roundToInt() + strokeWidth / 2,
                        sinPeriodLength = separator.sinPeriodLength.roundToPx(),
                        sinHeight = size.height.roundToInt() - dimens.strokeWidth.roundToPx(),
                    )
                    path.translate(Offset(0f, strokeWidth / 2f))
                    drawPath(path, color, style = Stroke(strokeWidth.toFloat()))
                    path.rewind()
                }
            }
        }
    )

@Composable
fun SinHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline,
) {
    Spacer(
        modifier =
            modifier.height(LocalDimens.current.divider.sinHeight).fillMaxWidth().sinDivider(color)
    )
}

@LightAndDarkThemePreview
@Composable
fun SinHorizontalDividerPreview() {
    LiftAppTheme { Surface { SinHorizontalDivider() } }
}
