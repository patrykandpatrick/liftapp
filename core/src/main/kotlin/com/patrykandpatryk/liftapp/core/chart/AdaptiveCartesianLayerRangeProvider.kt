package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sign

private class AdaptiveCartesianLayerRangeProvider(
    private val extentValue: Double,
    private val xAxisCartesianLayerRangeProvider: CartesianLayerRangeProvider,
) : CartesianLayerRangeProvider {
    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double =
        (minY - extentValue).round(maxY + extentValue, isMin = true)

    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double =
        (maxY + extentValue).round(minY - extentValue, isMin = false)

    override fun getMinX(minX: Double, maxX: Double, extraStore: ExtraStore): Double =
        xAxisCartesianLayerRangeProvider.getMinX(minX, maxX, extraStore)

    override fun getMaxX(minX: Double, maxX: Double, extraStore: ExtraStore): Double =
        xAxisCartesianLayerRangeProvider.getMaxX(minX, maxX, extraStore)

    private fun Double.round(other: Double, isMin: Boolean): Double {
        val absoluteValue = abs(this)
        val base = 10.0.pow(floor(log10(max(absoluteValue, abs(other)))) - 1)
        return sign *
            (if (isMin) floor(absoluteValue / base) else ceil(absoluteValue / base)) *
            base
    }
}

@Composable
fun rememberAdaptiveCartesianLayerRangeProvider(
    extentValue: Double = 1.0,
    xAxisCartesianLayerRangeProvider: CartesianLayerRangeProvider =
        CartesianLayerRangeProvider.auto(),
): CartesianLayerRangeProvider =
    remember(extentValue) {
        AdaptiveCartesianLayerRangeProvider(extentValue, xAxisCartesianLayerRangeProvider)
    }
