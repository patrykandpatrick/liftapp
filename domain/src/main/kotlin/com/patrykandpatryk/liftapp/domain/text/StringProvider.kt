package com.patrykandpatryk.liftapp.domain.text

import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit

interface StringProvider {

    val dateFormatShort: String
    val dateFormatLong: String
    val timeFormatShort24h: String
    val timeFormatShort12h: String
    val timeFormatLong24h: String
    val timeFormatLong12h: String

    fun getDisplayUnit(unit: MassUnit): String

    fun getDisplayUnit(unit: DistanceUnit): String
}
