package com.patrykandpatryk.liftapp.testing

import com.patrykandpatryk.liftapp.domain.extension.getTypeErrorMessage
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.PercentageUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit

object TestStringProvider : StringProvider {

    override val dateFormatShort: String = "d MMMM"
    override val dateFormatLong: String = "EEEE, d MMMM"
    override val timeFormatShort24h: String = "HH:mm"
    override val timeFormatShort12h: String = "hh:mm a"
    override val timeFormatLong24h: String = "HH:mm:ss"
    override val timeFormatLong12h: String = "hh:mm:ss a"

    override fun getDisplayUnit(unit: ValueUnit, respectLeadingSpaceSetting: Boolean): String = when (unit) {
        MassUnit.Kilograms -> "kg"
        MassUnit.Pounds -> "lb"
        LongDistanceUnit.Kilometer -> "km"
        LongDistanceUnit.Mile -> "mi"
        MediumDistanceUnit.Meter -> "m"
        MediumDistanceUnit.Foot -> "ft"
        ShortDistanceUnit.Centimeter -> "cm"
        ShortDistanceUnit.Inch -> "in"
        PercentageUnit -> "%"
        else -> getTypeErrorMessage(unit = unit)
    }.let { displayUnit -> if (unit.hasLeadingSpace) " $displayUnit" else displayUnit }
}
