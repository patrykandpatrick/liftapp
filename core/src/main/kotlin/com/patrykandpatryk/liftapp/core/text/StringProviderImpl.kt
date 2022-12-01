package com.patrykandpatryk.liftapp.core.text

import android.app.Application
import androidx.annotation.StringRes
import com.patrykandpatryk.liftapp.core.R.string
import com.patrykandpatryk.liftapp.core.extension.stringResourceId
import com.patrykandpatryk.liftapp.core.ui.resource.stringRes
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import javax.inject.Inject

class StringProviderImpl @Inject constructor(
    private val application: Application,
) : StringProvider {

    override val andInAList: String
        get() = string(string.and_in_a_list)

    override val name: String
        get() = string(string.generic_name)

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

    private fun string(
        @StringRes id: Int,
        vararg formatArgs: Any,
    ): String = application.getString(id, *formatArgs)
}
