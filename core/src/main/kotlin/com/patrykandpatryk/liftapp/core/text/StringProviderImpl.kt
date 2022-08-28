package com.patrykandpatryk.liftapp.core.text

import android.app.Application
import androidx.annotation.StringRes
import com.patrykandpatryk.liftapp.core.R.string
import com.patrykandpatryk.liftapp.core.extension.stringResourceId
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import javax.inject.Inject

class StringProviderImpl @Inject constructor(
    private val application: Application,
) : StringProvider {

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

    override fun getDisplayUnit(unit: ValueUnit, respectLeadingSpaceSetting: Boolean): String =
        string(unit.stringResourceId)
            .let { displayUnit -> if (unit.hasLeadingSpace) " $displayUnit" else displayUnit }

    private fun string(@StringRes id: Int): String =
        application.getString(id)
}
