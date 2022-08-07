package com.patrykandpatryk.liftapp.domain.model

import com.patrykandpatryk.liftapp.domain.date.HourFormat
import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit

data class AllPreferences(
    val massUnit: MassUnit,
    val distanceUnit: DistanceUnit,
    val hourFormat: HourFormat,
)
