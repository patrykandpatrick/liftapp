package com.patrykandpatryk.liftapp.feature.settings.model

import com.patrykandpatryk.liftapp.domain.date.HourFormat
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit

sealed interface Action {

    data class SetMassUnit(val massUnit: MassUnit) : Action

    data class SetDistanceUnit(val distanceUnit: LongDistanceUnit) : Action

    data class SetHourFormat(val hourFormat: HourFormat) : Action

    data object PopBackStack : Action
}
