package com.patrykandpatryk.liftapp.domain.model

import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit

data class AllPreferences(
    val distanceUnit: DistanceUnit,
    val massUnit: MassUnit,
)
