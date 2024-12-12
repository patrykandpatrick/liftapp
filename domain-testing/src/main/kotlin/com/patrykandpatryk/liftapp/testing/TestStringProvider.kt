package com.patrykandpatryk.liftapp.testing

import com.patrykandpatryk.liftapp.domain.extension.getTypeErrorMessage
import com.patrykandpatryk.liftapp.domain.goal.Goal
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
    override val dateFormatEdit: String = "dd.MM.yyyy"
    override val dateFormatFull: String = "d MMMM YYYY"

    override val errorMustBeHigherThanZero: String = "The value must be higher than zero."

    override fun getDisplayUnit(unit: ValueUnit, respectLeadingSpaceSetting: Boolean): String =
        when (unit) {
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

    override fun toPrettyString(goal: Goal): String =
        "%s–%s × %s".format(goal.minReps, goal.maxReps, goal.sets)

    override fun fieldCannotBeEmpty(): String = "This field cannot be empty."

    override fun fieldMustBeHigherThanZero(): String = "This field must be higher than zero."

    override fun fieldMustBeHigherOrEqualTo(value: String): String =
        "The value must be higher, or equal to %s.".format(value)

    override fun fieldTooShort(actual: Int, minLength: Int): String =
        "This field must be at least %d characters long.".format(minLength)

    override fun fieldTooLong(actual: Int, maxLength: Int): String =
        "This field must be at most %d characters long.".format(maxLength)

    override fun valueTooSmall(minValue: String): String =
        "This field must be at least %s.".format(minValue)

    override fun valueTooBig(maxValue: String): String =
        "This field must be at most %s.".format(maxValue)

    override fun valueNotValidNumber(): String = "This field must be a valid number."

    override fun doesNotEqual(formula: String, actual: String): String =
        "$formula does not equal $actual"
}
