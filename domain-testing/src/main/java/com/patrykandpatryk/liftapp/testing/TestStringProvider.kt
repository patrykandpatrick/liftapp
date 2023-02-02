package com.patrykandpatryk.liftapp.testing

import com.patrykandpatryk.liftapp.domain.extension.getTypeErrorMessage
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.PercentageUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit

object TestStringProvider : StringProvider {

    override val andInAList: String = "&"
    override val name: String = "Name"

    override val list: String = "List"

    override val dateFormatShort: String = "d MMMM"
    override val dateFormatLong: String = "EEEE, d MMMM"
    override val timeFormatShort24h: String = "HH:mm"
    override val timeFormatShort12h: String = "hh:mm a"
    override val timeFormatLong24h: String = "HH:mm:ss"
    override val timeFormatLong12h: String = "hh:mm:ss a"

    override val errorMustBeHigherThanZero: String = "The value must be higher than zero."

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

    override fun quoted(value: String): String = "”%s“".format(value)

    override fun getErrorCannotBeEmpty(name: String): String = "%s cannot be empty.".format(name)

    override fun getMuscleName(muscle: Muscle): String = muscle.name

    override fun getErrorNameTooLong(actual: Int, limit: Int): String =
        "The name is too long (%1\$d/%2\$d).".format(actual, limit)

    override fun getResolvedName(name: Name): String =
        when (name) {
            is Name.Raw -> name.value
            is Name.Resource -> requireNotNull(name.resource.resourceId::class.simpleName)
        }
}
