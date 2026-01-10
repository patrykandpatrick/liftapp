package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.common.data.ExtraStore

class ExtraStoreCartesianLayerRangeProvider : CartesianLayerRangeProvider {
    override fun getMaxX(minX: Double, maxX: Double, extraStore: ExtraStore): Double =
        extraStore[ExtraStoreKey.MaxX]

    override fun getMinX(minX: Double, maxX: Double, extraStore: ExtraStore): Double =
        extraStore[ExtraStoreKey.MinX]
}

@Composable
fun rememberExtraStoreCartesianLayerRangeProvider(): CartesianLayerRangeProvider = remember {
    ExtraStoreCartesianLayerRangeProvider()
}
