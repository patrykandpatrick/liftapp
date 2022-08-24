package com.patrykandpatryk.liftapp.testing

import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit

object TestStringProvider : StringProvider {

    override val dateFormatShort: String = "d MMMM"
    override val dateFormatLong: String = "EEEE, d MMMM"
    override val timeFormatShort24h: String = "HH:mm"
    override val timeFormatShort12h: String = "hh:mm a"
    override val timeFormatLong24h: String = "HH:mm:ss"
    override val timeFormatLong12h: String = "hh:mm:ss a"

    override fun getDisplayUnit(unit: MassUnit): String = when (unit) {
        MassUnit.Kilograms -> "kg"
        MassUnit.Pounds -> "lb"
    }

    override fun getDisplayUnit(unit: DistanceUnit): String = when (unit) {
        DistanceUnit.Kilometers -> "km"
        DistanceUnit.Miles -> "mi"
    }
}
