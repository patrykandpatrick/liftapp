package com.patrykandpatryk.liftapp.domain.model

import com.patrykandpatryk.liftapp.domain.date.HourFormat
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit

data class AllPreferences(
    val massUnit: MassUnit,
    val longDistanceUnit: LongDistanceUnit,
    val mediumDistanceUnit: MediumDistanceUnit,
    val shortDistanceUnit: ShortDistanceUnit,
    val hourFormat: HourFormat,
)
