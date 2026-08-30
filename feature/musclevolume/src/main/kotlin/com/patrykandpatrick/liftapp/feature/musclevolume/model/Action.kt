package com.patrykandpatrick.liftapp.feature.musclevolume.model

import com.patrykandpatrick.liftapp.domain.date.DateInterval

sealed interface Action {

    data object PopBackStack : Action

    data class SetDateInterval(val dateInterval: DateInterval) : Action

    data object IncrementDateInterval : Action

    data object DecrementDateInterval : Action
}
