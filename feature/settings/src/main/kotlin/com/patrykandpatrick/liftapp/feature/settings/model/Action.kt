package com.patrykandpatrick.liftapp.feature.settings.model

import com.patrykandpatrick.liftapp.domain.date.HourFormat
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit

sealed interface Action {

    data class SetMassUnit(val massUnit: MassUnit) : Action

    data class SetDistanceUnit(val distanceUnit: LongDistanceUnit) : Action

    data class SetHourFormat(val hourFormat: HourFormat) : Action

    data object PopBackStack : Action
}
