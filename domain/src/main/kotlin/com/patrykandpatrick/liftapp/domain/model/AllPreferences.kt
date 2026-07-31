package com.patrykandpatrick.liftapp.domain.model

import com.patrykandpatrick.liftapp.domain.date.HourFormat
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.ShortDistanceUnit

data class AllPreferences(
    val massUnit: MassUnit,
    val longDistanceUnit: LongDistanceUnit,
    val mediumDistanceUnit: MediumDistanceUnit,
    val shortDistanceUnit: ShortDistanceUnit,
    val hourFormat: HourFormat,
)
