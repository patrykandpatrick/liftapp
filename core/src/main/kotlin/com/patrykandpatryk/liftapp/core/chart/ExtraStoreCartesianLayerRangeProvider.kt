package com.patrykandpatryk.liftapp.core.chart

import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.common.data.ExtraStore

class ExtraStoreCartesianLayerRangeProvider : CartesianLayerRangeProvider {
    override fun getMaxX(minX: Double, maxX: Double, extraStore: ExtraStore): Double =
        extraStore[ExtraStoreKey.MaxX]

    override fun getMinX(minX: Double, maxX: Double, extraStore: ExtraStore): Double =
        extraStore[ExtraStoreKey.MinX]
}
