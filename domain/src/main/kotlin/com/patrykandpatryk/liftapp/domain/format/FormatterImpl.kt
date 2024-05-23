package com.patrykandpatryk.liftapp.domain.format

import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import kotlinx.coroutines.flow.first
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject

class FormatterImpl @Inject constructor(
    private val stringProvider: StringProvider,
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
        Formatter.DateFormat.DateShort -> stringProvider.dateFormatShort
        Formatter.DateFormat.DateLong -> stringProvider.dateFormatLong
    }.let(::SimpleDateFormat)

    private suspend fun getTimeShortPattern(): String =
        if (is24H()) {
            stringProvider.timeFormatShort24h
        } else {
            stringProvider.timeFormatShort12h
        }

    private suspend fun getTimeLongPattern(): String =
        if (is24H()) {
            stringProvider.timeFormatLong24h
        } else {
            stringProvider.timeFormatLong12h
        }

    override fun formatNumber(
        vararg numbers: Number,
        format: Formatter.NumberFormat,
        separator: String,
        prefix: String?,
        postfix: String?,
    ): String = numbers.joinToString(
        separator = separator,
        prefix = prefix.orEmpty(),
        postfix = postfix.orEmpty(),
    ) { number ->
        when (format) {
            Formatter.NumberFormat.Decimal -> decimalFormat.format(number)
            Formatter.NumberFormat.Integer -> integerFormat.format(number)
        }
    }

    override fun parseFloatOrZero(value: String): Float =
        try {
            decimalFormat.parse(value)?.toFloat()
        } catch (exception: ParseException) {
            null
        } ?: 0f

    override suspend fun is24H(): Boolean = preferences.is24H.first()
}
