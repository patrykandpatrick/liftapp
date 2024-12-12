package com.patrykandpatryk.liftapp.domain.format

import com.patrykandpatryk.liftapp.domain.Constants.Format.DECIMAL_PATTERN
import com.patrykandpatryk.liftapp.domain.Constants.Format.INTEGER_PATTERN
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class Formatter(private val stringProvider: StringProvider, private val is24H: Flow<Boolean>) {
    @Inject
    constructor(
        stringProvider: StringProvider,
        preferenceRepository: PreferenceRepository,
    ) : this(stringProvider, preferenceRepository.is24H)

    private val decimalSymbols = DecimalFormatSymbols(Locale.getDefault())

    private val decimalOutputFormat = DecimalFormat("#,###.##", decimalSymbols)

    private val decimalInputFormat = DecimalFormat(DECIMAL_PATTERN, decimalSymbols)

    private val integerOutputFormat = DecimalFormat(INTEGER_PATTERN, decimalSymbols)

    private val percentFormat = DecimalFormat("#%")

    private val decimalNumberRegex = """-?\d+${decimalSymbols.decimalSeparator}?\d*""".toRegex()

    private suspend fun formatDate(localDateTime: LocalDateTime, dateFormat: DateFormat): String =
        localDateTime.format(DateTimeFormatter.ofPattern(dateFormat.getPattern()))

    private suspend fun DateFormat.getPattern(): String =
        when (this) {
            DateFormat.MinutesSeconds -> MINUTES_SECONDS
            DateFormat.HoursMinutes -> if (is24H()) HOURS_MINUTES_24H else HOURS_MINUTES_12H
            DateFormat.HoursMinutesSeconds ->
                if (is24H()) HOURS_MINUTES_SECONDS_24H else HOURS_MINUTES_SECONDS_12H
            DateFormat.DateShort -> stringProvider.dateFormatShort
            DateFormat.DateLong -> stringProvider.dateFormatLong
            DateFormat.DateFull -> stringProvider.dateFormatFull
            DateFormat.DateEdit -> stringProvider.dateFormatEdit
        }

    suspend fun getFormattedDate(localDateTime: LocalDateTime): FormattedDate =
        FormattedDate(
            formatDate(localDateTime, DateFormat.DateShort),
            formatDate(localDateTime, DateFormat.DateLong),
            formatDate(localDateTime, DateFormat.HoursMinutes),
            formatDate(localDateTime, DateFormat.HoursMinutesSeconds),
            localDateTime,
        )

    suspend fun getFormattedDuration(duration: Duration, dateFormat: DateFormat): String =
        SimpleDateFormat(dateFormat.getPattern(), Locale.getDefault())
            .format(duration.inWholeMilliseconds)

    suspend fun is24H(): Boolean = is24H.first()

    fun formatNumber(
        vararg numbers: Number,
        format: NumberFormat,
        separator: String = " | ",
        prefix: String? = null,
        postfix: String? = null,
    ): String =
        numbers.joinToString(
            separator = separator,
            prefix = prefix.orEmpty(),
            postfix = postfix.orEmpty(),
        ) { number ->
            when (format) {
                NumberFormat.Decimal -> decimalInputFormat.format(number)
                NumberFormat.Integer -> integerOutputFormat.format(number)
            }
        }

    fun formatWeight(weight: Float, massUnit: MassUnit): String =
        "${formatNumber(weight, format = NumberFormat.Decimal)}${stringProvider.getDisplayUnit(massUnit)}"

    fun toDoubleOrNull(value: String): Double? =
        try {
            decimalInputFormat.parse(value)?.toDouble()
        } catch (_: ParseException) {
            null
        }

    fun toInputDecimalNumber(value: Double): String = decimalInputFormat.format(value)

    fun isValidNumber(value: String): Boolean = decimalNumberRegex.matches(value)

    fun round(value: Double): Double =
        decimalInputFormat.parse(decimalInputFormat.format(value)).toDouble()

    enum class DateFormat {
        HoursMinutes,
        HoursMinutesSeconds,
        MinutesSeconds,
        DateShort,
        DateLong,
        DateFull,
        DateEdit,
    }

    enum class NumberFormat {
        Decimal,
        Integer,
    }

    companion object Pattern {
        private const val MINUTES_SECONDS = "mm:ss"
        private const val HOURS_MINUTES_24H = "HH:mm"
        private const val HOURS_MINUTES_12H = "h:mm a"
        private const val HOURS_MINUTES_SECONDS_24H = "HH:mm:ss"
        private const val HOURS_MINUTES_SECONDS_12H = "h:mm:ss a"
    }
}
