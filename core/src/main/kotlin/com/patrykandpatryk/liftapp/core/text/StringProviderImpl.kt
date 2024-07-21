package com.patrykandpatryk.liftapp.core.text

import android.content.Context
import androidx.annotation.StringRes
import com.patrykandpatryk.liftapp.core.R.string
import com.patrykandpatryk.liftapp.core.extension.stringResourceId
import com.patrykandpatryk.liftapp.core.ui.resource.stringRes
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import javax.inject.Inject

class StringProviderImpl @Inject constructor(
    private val context: Context,
) : StringProvider {

    override val andInAList: String
        get() = string(string.and_in_a_list)

    override val name: String
        get() = string(string.generic_name)

    override val list: String
        get() = string(string.generic_list)

    override val dateFormatShort: String
        get() = string(string.date_format_short)

    override val dateFormatLong: String
        get() = string(string.date_format_long)

    override val timeFormatShort24h: String
        get() = string(string.time_format_short_24h)

    override val timeFormatShort12h: String
        get() = string(string.time_format_short_12h)

    override val timeFormatLong24h: String
        get() = string(string.time_format_long_24h)

    override val timeFormatLong12h: String
        get() = string(string.time_format_long_12h)

    override val errorMustBeHigherThanZero: String
        get() = string(string.error_must_be_higher_than_zero)

    override val dateFormatFull: String
        get() = string(string.date_format_full)

    override val dateFormatEdit: String
        get() = string(string.date_format_edit)

    override fun getDisplayUnit(unit: ValueUnit, respectLeadingSpaceSetting: Boolean): String =
        string(unit.stringResourceId)
            .let { displayUnit -> if (unit.hasLeadingSpace) " $displayUnit" else displayUnit }

    override fun quoted(value: String): String = string(string.generic_quoted, value)

    override fun getErrorNameTooLong(actual: Int, limit: Int): String =
        string(string.error_name_too_long, actual, limit)

    override fun getErrorCannotBeEmpty(name: String): String =
        string(string.error_x_empty, name)

    override fun getMuscleName(muscle: Muscle): String =
        string(muscle.stringRes)

    override fun toPrettyString(goal: Goal): String =
        string(string.goal_format, goal.minReps, goal.maxReps, goal.sets)

    override fun getResolvedName(name: Name): String = when (name) {
        is Name.Raw -> name.value
        is Name.Resource -> context.getString(name.resource.resourceId)
    }

    override fun fieldTooShort(actual: Int, minLength: Int): String =
        string(string.error_field_too_short, minLength, actual, minLength)

    override fun fieldTooLong(actual: Int, maxLength: Int): String =
        string(string.error_field_too_long, maxLength, actual, maxLength)

    override fun valueTooSmall(minValue: String): String =
        string(string.error_value_too_small, minValue)

    override fun valueTooBig(maxValue: String): String =
        string(string.error_value_too_big, maxValue)

    override fun valueNotValidNumber(): String = string(string.error_value_not_valid_number)

    override fun fieldCannotBeEmpty(): String = string(string.error_field_cannot_be_empty, name)

    override fun fieldMustBeHigherThanZero(): String = string(string.error_must_be_higher_than_zero)

    override fun doesNotEqual(actual: String, expected: String): String = string(string.error_does_not_equal, actual, expected)
    private fun string(
        @StringRes id: Int,
        vararg formatArgs: Any,
    ): String = if (formatArgs.isNotEmpty()) context.getString(id, *formatArgs) else context.getString(id)
}
