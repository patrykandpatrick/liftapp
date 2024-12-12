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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import kotlin.math.roundToInt
import kotlin.math.sin

private fun Modifier.sinDivider(
    color: Color,
    topBackgroundColor: Color = Color.Transparent,
    bottomBackgroundColor: Color = Color.Transparent,
) =
    then(
        Modifier.composed {
            val dimens = LocalDimens.current
            val separator = dimens.divider
            val path = remember { Path() }
            val topPath =
                remember(topBackgroundColor == Color.Transparent) {
                    if (topBackgroundColor == Color.Transparent) null else Path()
                }
            val bottomPath =
                remember(bottomBackgroundColor == Color.Transparent) {
                    if (bottomBackgroundColor == Color.Transparent) null else Path()
                }

            drawWithCache {
                onDrawBehind {
                    for (x in 0 until size.width.roundToInt()) {
                        val y =
                            sin(x.toFloat() / separator.sinPeriodLength.toPx()) *
                                (separator.sinHeight.toPx() / 2 - 2) + size.height -
                                separator.sinHeight.toPx() / 2
                        if (x == 0) {
                            path.moveTo(0f, y)
                        } else {
                            path.lineTo(x.toFloat(), y)
                        }
                    }
                    drawPath(path, color, style = Stroke(dimens.strokeWidth.toPx()))

                    topPath?.apply {
                        addPath(path)
                        lineTo(size.width, 0f)
                        lineTo(0f, 0f)
                        close()
                        drawPath(this, topBackgroundColor)
                        rewind()
                    }

                    bottomPath?.apply {
                        addPath(path)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                        drawPath(this, bottomBackgroundColor)
                        rewind()
                    }

                    path.rewind()
                }
            }
        }
    )

@Composable
fun SinHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline,
    topBackgroundColor: Color = Color.Transparent,
    bottomBackgroundColor: Color = Color.Transparent,
) {
    Spacer(
        modifier =
            modifier
                .height(LocalDimens.current.divider.sinHeight)
                .fillMaxWidth()
                .sinDivider(color, topBackgroundColor, bottomBackgroundColor)
    )
}

@LightAndDarkThemePreview
@Composable
fun SinHorizontalDividerPreview() {
    LiftAppTheme { Surface { SinHorizontalDivider() } }
}
