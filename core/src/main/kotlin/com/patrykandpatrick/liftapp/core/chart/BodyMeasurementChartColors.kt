package com.patrykandpatrick.liftapp.core.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * The colors a measurement's own readings take, so that everything drawing them — a card's
 * sparkline, the details screen's chart, the legend naming the two sides — draws the same series
 * the same way. The pairing matches
 * [com.patrykandpatrick.liftapp.domain.bodymeasurement.FormatBodyMeasurementValueToStringUseCase],
 * which already marks a left-side change green and a right-side one yellow.
 */
object BodyMeasurementChartColors {
    /** A single reading, or the left of a two-sided pair. */
    val leading: Color
        @Composable get() = colorScheme.chartColors[0]

    /** The right of a two-sided pair. */
    val trailing: Color
        @Composable get() = colorScheme.chartColors[1]
}
