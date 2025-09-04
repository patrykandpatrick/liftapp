package com.patrykandpatryk.liftapp.core.chart

import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel

data object OnModelChangeAutoScrollCondition : AutoScrollCondition {

    override fun shouldScroll(
        oldModel: CartesianChartModel?,
        newModel: CartesianChartModel,
    ): Boolean = oldModel?.id != newModel.id
}

val AutoScrollCondition.Companion.OnModelChange: AutoScrollCondition
    get() = OnModelChangeAutoScrollCondition
