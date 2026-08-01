package com.patrykandpatrick.liftapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * The fill under a chart's line: the line's own color at [TopAlpha] where the line runs, fading to
 * nothing at the baseline. Shared so every chart reads as one family, whether it is a full
 * [com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer] or a sparkline.
 */
object ChartAreaFill {
    const val TopAlpha = .3f

    const val BottomAlpha = 0f

    fun brush(color: Color): Brush =
        Brush.verticalGradient(
            listOf(color.copy(alpha = TopAlpha), color.copy(alpha = BottomAlpha))
        )
}
