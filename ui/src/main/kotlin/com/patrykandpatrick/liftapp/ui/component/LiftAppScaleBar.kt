package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * Where one value sits on a scale whose whole range is drawn, as a thumb over a coloured track.
 *
 * Unlike [LiftAppLinearProgressIndicator], the shape claims nothing about direction: right for a
 * quantity whose desirable range is in the middle and whose top end is a limit, not a target.
 *
 * [colorStops] are the scale itself. [marks] are fixed points on it worth naming; what they mean
 * belongs somewhere it can be said once, not on every bar.
 */
@Composable
fun LiftAppScaleBar(
    position: Float,
    colorStops: List<Pair<Float, Color>>,
    thumbColor: Color,
    modifier: Modifier = Modifier,
    marks: List<Float> = emptyList(),
    markColor: Color = LiftAppScaleBarDefaults.markColor,
    thickness: Dp = LiftAppScaleBarDefaults.thickness,
    thumbWidth: Dp = LiftAppScaleBarDefaults.thumbWidth,
    thumbHeight: Dp = LiftAppScaleBarDefaults.thumbHeight,
    markSize: Dp = LiftAppScaleBarDefaults.markSize,
) {
    val fraction = position.coerceIn(0f, 1f)

    Spacer(
        modifier.fillMaxWidth().height(thumbHeight).drawWithCache {
            val trackThickness = thickness.toPx()
            val thumb = Size(thumbWidth.toPx(), thumbHeight.toPx())

            // Between the extreme thumb centres, not edge to edge, so the gradient shares one
            // coordinate frame with the marks and the thumb.
            val brush =
                Brush.horizontalGradient(
                    colorStops = colorStops.toTypedArray(),
                    startX = thumb.width / 2,
                    endX = size.width - thumb.width / 2,
                )

            onDrawBehind {
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(0f, (size.height - trackThickness) / 2),
                    size = Size(size.width, trackThickness),
                    cornerRadius = CornerRadius(trackThickness / 2),
                )

                marks.forEach { mark ->
                    drawCircle(
                        color = markColor,
                        radius = markSize.toPx() / 2,
                        center =
                            Offset(thumbCenter(mark, size.width, thumb.width), size.height / 2),
                    )
                }

                drawRoundRect(
                    color = thumbColor,
                    topLeft =
                        Offset(
                            thumbCenter(fraction, size.width, thumb.width) - thumb.width / 2,
                            (size.height - thumb.height) / 2,
                        ),
                    size = thumb,
                    cornerRadius = CornerRadius(thumb.width / 2),
                )
            }
        }
    )
}

/** Travel is the width less the thumb, so neither extreme overhangs the track. */
private fun thumbCenter(position: Float, width: Float, thumbWidth: Float): Float =
    thumbWidth / 2 + (width - thumbWidth) * position.coerceIn(0f, 1f)

object LiftAppScaleBarDefaults {
    val markSize: Dp
        @Composable get() = dimens.scaleBar.markSize

    /**
     * Taken from the foreground rather than the background: a bar of this appears on a card as
     * readily as on a page, and a background-coloured mark would be right on only one of them.
     */
    val markColor: Color
        @Composable get() = colorScheme.foreground.copy(alpha = .45f)

    val thickness: Dp
        @Composable get() = dimens.scaleBar.thickness

    val thumbWidth: Dp
        @Composable get() = dimens.scaleBar.thumbWidth

    val thumbHeight: Dp
        @Composable get() = dimens.scaleBar.thumbHeight
}

@LightAndDarkThemePreview
@Composable
private fun LiftAppScaleBarPreview() {
    LiftAppTheme {
        LiftAppBackground {
            val stops =
                listOf(
                    0f to colorScheme.foregroundVariant,
                    .22f to colorScheme.foregroundVariant,
                    .26f to colorScheme.yellow,
                    .38f to colorScheme.yellow,
                    .42f to colorScheme.green,
                    .78f to colorScheme.green,
                    .82f to colorScheme.red,
                    1f to colorScheme.red,
                )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                // Both extremes included: the thumb has to stay inside the track at either end.
                listOf(0f, .12f, .32f, .6f, 1f).forEach { position ->
                    LiftAppScaleBar(
                        position = position,
                        colorStops = stops,
                        thumbColor = colorScheme.foreground,
                    )
                }
            }
        }
    }
}
