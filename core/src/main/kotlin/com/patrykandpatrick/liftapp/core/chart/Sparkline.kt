package com.patrykandpatrick.liftapp.core.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import kotlinx.coroutines.runBlocking

/**
 * Gives a [Sparkline]'s producer its series: [values] oldest to newest, plotted against their
 * indices, since a sparkline has no axis on which uneven spacing would mean anything.
 *
 * Hold the producer for as long as the sparkline may be shown and run this again as the readings
 * change, rather than making a producer per set of readings: a [CartesianChartHost] rejects a
 * producer it has not seen before, and it is the reuse that lets the line animate from the readings
 * the chart already holds to the new ones.
 */
suspend fun CartesianChartModelProducer.runSparklineTransaction(values: List<Number>) {
    runTransaction {
        lineModel { series(values) }
        // Which reading is the newest is a fact about the series, so it travels with it. Handing it
        // to the point provider instead would tie that provider to one series' length.
        extras { extras -> extras[ExtraStoreKey.LatestPointX] = values.lastIndex.toDouble() }
    }
}

/**
 * A trend line stripped to the line itself — no axes, no marker, no scrolling — for showing the
 * shape of a series beside the value it belongs to. It is the app's [rememberLine] under the hood,
 * so a sparkline and the full chart it summarizes are the same drawing at two sizes.
 *
 * [modelProducer] is fed by [runSparklineTransaction], which is also what marks the newest reading
 * for [showLatestPoint]. The line animates in, and animates again between readings as they change.
 * Its values are scaled to their own range, so the line shows relative movement and says nothing
 * about absolute magnitude — pair it with the value itself.
 */
@Composable
fun Sparkline(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
    color: Color = colorScheme.primary,
    strokeWidth: Dp = SparklineDefaults.strokeWidth,
    showLatestPoint: Boolean = false,
) {
    // Built unconditionally so that turning the point off does not change what the composition
    // holds; it is a shape component, and an unused one costs nothing to keep.
    val pointComponent = rememberLineCartesianLayerPointComponent(strokeColor = color)
    val pointProvider =
        remember(pointComponent, showLatestPoint) {
            if (showLatestPoint) {
                LatestPointProvider(
                    LineCartesianLayer.Point(
                        component = pointComponent,
                        size = SparklineDefaults.latestPointSize,
                    )
                )
            } else {
                null
            }
        }

    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider =
                        LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                color = color,
                                stroke =
                                    LineCartesianLayer.LineStroke.Continuous(
                                        thickness = strokeWidth,
                                        cap = StrokeCap.Round,
                                    ),
                                pointProvider = pointProvider,
                            )
                        ),
                    rangeProvider = SparklineRangeProvider,
                )
            ),
        modelProducer = modelProducer,
        modifier = modifier,
        // No scrolling, which also settles the zoom: with scrolling off, the host's default zoom
        // state is `Zoom.Content`, so the series is scaled to the width it is given.
        scrollState = rememberVicoScrollState(scrollEnabled = false),
    )
}

object SparklineDefaults {
    val strokeWidth = 2.dp

    /** The diameter of the point marking the newest reading, when one is shown. */
    val latestPointSize = 12.dp
}

/**
 * Marks the newest reading alone, which it finds through [ExtraStoreKey.LatestPointX]: the provider
 * then holds styling and no data, so one instance serves a series of any length and outlives a
 * change to the readings. A series set without [runSparklineTransaction] carries no such extra and
 * gets no marked point.
 *
 * [getLargestPoint] reports the point even where none is drawn, which is what the layer measures to
 * keep the marker clear of the chart's edges.
 */
private data class LatestPointProvider(private val point: LineCartesianLayer.Point) :
    LineCartesianLayer.PointProvider {
    override fun getPoint(
        entry: LineCartesianLayerModel.Entry,
        extraStore: ExtraStore,
    ): LineCartesianLayer.Point? = point.takeIf {
        entry.x == extraStore.getOrNull(ExtraStoreKey.LatestPointX)
    }

    override fun getLargestPoint(extraStore: ExtraStore): LineCartesianLayer.Point = point
}

/**
 * Keeps the series' own range. [CartesianLayerRangeProvider.auto] anchors the range at zero, which
 * is right for a chart that is read against an axis but wrong here: body measurements sit far from
 * zero and move within a few units of each other, so anchoring would flatten every sparkline into a
 * straight line at the top.
 */
private object SparklineRangeProvider : CartesianLayerRangeProvider {
    /** Half the room given to a series whose values are all equal. */
    private const val FLAT_SERIES_EXTENT = 1.0

    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double =
        if (minY == maxY) minY - FLAT_SERIES_EXTENT else minY

    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double =
        if (minY == maxY) maxY + FLAT_SERIES_EXTENT else maxY
}

@Composable
private fun previewProducer(values: List<Number>) =
    remember(values) {
        CartesianChartModelProducer().also { producer ->
            runBlocking { producer.runSparklineTransaction(values) }
        }
    }

@LightAndDarkThemePreview
@Composable
private fun SparklinePreview() {
    PreviewTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Sparkline(
                modelProducer = previewProducer(listOf(82.1, 81.4, 81.6, 80.2, 80.5, 79.1, 78.4)),
                showLatestPoint = true,
                modifier = Modifier.fillMaxWidth().height(64.dp),
            )

            Sparkline(
                modelProducer = previewProducer(listOf(37.2, 37.4, 37.3, 37.9, 38.2)),
                strokeWidth = 1.5.dp,
                modifier = Modifier.size(56.dp, 20.dp),
            )

            // A series that never moves still has to draw: down the middle, saying as much.
            Sparkline(
                modelProducer = previewProducer(List(4) { 70.0 }),
                modifier = Modifier.size(120.dp, 32.dp),
            )
        }
    }
}
