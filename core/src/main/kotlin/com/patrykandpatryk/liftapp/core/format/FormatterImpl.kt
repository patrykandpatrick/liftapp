package com.patrykandpatryk.liftapp.core.format

import android.app.Application
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.format.FormattedDate
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.repository.PreferenceRepository
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.first

internal class FormatterImpl @Inject constructor(
    private val application: Application,
    private val preferences: PreferenceRepository,
) : Formatter {

    private val decimalFormat = DecimalFormat("#.##")

    private val integerFormat = DecimalFormat("#")

    override suspend fun formatDate(
        millis: Long,
        dateFormat: Formatter.DateFormat,
    ): String = dateFormat.getSimpleDateFormat().format(millis)

    override suspend fun getFormattedDate(
        millis: Long,
    ): FormattedDate {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return getFormattedDate(calendar = calendar)
    }

    override suspend fun getFormattedDate(
        calendar: Calendar,
    ): FormattedDate = FormattedDateImpl(
        dateShort = formatDate(
            millis = calendar.timeInMillis,
            dateFormat = Formatter.DateFormat.DateShort,
        ),
        dateLong = formatDate(
            millis = calendar.timeInMillis,
            dateFormat = Formatter.DateFormat.DateLong,
        ),
        timeShort = formatDate(
            millis = calendar.timeInMillis,
            dateFormat = Formatter.DateFormat.TimeShort,
        ),
        timeLong = formatDate(
            millis = calendar.timeInMillis,
            dateFormat = Formatter.DateFormat.TimeLong,
        ),
        calendar = calendar,
    )

    private suspend fun Formatter.DateFormat.getSimpleDateFormat(): SimpleDateFormat = when (this) {
        Formatter.DateFormat.TimeShort -> getTimeShortPattern()
        Formatter.DateFormat.TimeLong -> getTimeLongPattern()
        Formatter.DateFormat.DateShort -> application.getString(R.string.date_format_short)
        Formatter.DateFormat.DateLong -> application.getString(R.string.date_format_long)
    }.let(::SimpleDateFormat)

    private suspend fun getTimeShortPattern(): String =
        if (is24H()) {
            R.string.time_format_short_24h
        } else {
            R.string.time_format_short_12h
        }.let(application::getString)

    private suspend fun getTimeLongPattern(): String =
        if (is24H()) {
            R.string.time_format_long_24h
        } else {
            R.string.time_format_long_12h
        }.let(application::getString)

    override fun formatNumber(
        number: Number,
        format: Formatter.NumberFormat,
    ): String = when (format) {
        Formatter.NumberFormat.Decimal -> decimalFormat.format(number)
        Formatter.NumberFormat.Integer -> integerFormat.format(number)
    }

    override fun parseFloatOrZero(value: String): Float =
        try {
            decimalFormat.parse(value)?.toFloat()
        } catch (exception: ParseException) {
            null
        } ?: 0f

    override suspend fun is24H(): Boolean = preferences.is24H.first()
}
