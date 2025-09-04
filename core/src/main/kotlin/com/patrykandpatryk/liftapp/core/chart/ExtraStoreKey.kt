package com.patrykandpatryk.liftapp.core.chart

import com.patrykandpatrick.vico.core.common.data.ExtraStore

object ExtraStoreKey {
    data object MinX : ExtraStore.Key<Double>()

    data object MaxX : ExtraStore.Key<Double>()

    data object DateInterval :
        ExtraStore.Key<com.patrykandpatryk.liftapp.domain.date.DateInterval>()

    data object ValueUnit : ExtraStore.Key<com.patrykandpatryk.liftapp.domain.unit.ValueUnit>()
}
