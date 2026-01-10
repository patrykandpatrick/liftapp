package com.patrykandpatryk.liftapp.core.chart

import com.patrykandpatrick.vico.compose.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel

data object OnModelChangeAutoScrollCondition : AutoScrollCondition {

    override fun shouldScroll(
        oldModel: CartesianChartModel?,
        newModel: CartesianChartModel,
    ): Boolean = oldModel != newModel
}

val AutoScrollCondition.Companion.OnModelChange: AutoScrollCondition
    get() = OnModelChangeAutoScrollCondition
