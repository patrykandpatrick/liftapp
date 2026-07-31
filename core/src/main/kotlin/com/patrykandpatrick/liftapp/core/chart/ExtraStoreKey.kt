package com.patrykandpatrick.liftapp.core.chart

import com.patrykandpatrick.vico.compose.common.data.ExtraStore

object ExtraStoreKey {
    data object MinX : ExtraStore.Key<Double>()

    data object MaxX : ExtraStore.Key<Double>()

    data object DateInterval :
        ExtraStore.Key<com.patrykandpatrick.liftapp.domain.date.DateInterval>()

    data object ValueUnit : ExtraStore.Key<com.patrykandpatrick.liftapp.domain.unit.ValueUnit>()

    data object ShowLeftRightLegend : ExtraStore.Key<Boolean>()
}
