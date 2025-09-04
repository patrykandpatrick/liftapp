package com.patrykandpatryk.liftapp.domain.format

import com.patrykandpatryk.liftapp.domain.Constants.Format.DECIMAL_PATTERN
import com.patrykandpatryk.liftapp.domain.Constants.Format.INTEGER_PATTERN
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.DurationUnit
import com.patrykandpatryk.liftapp.domain.unit.EnergyUnit
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.PercentageUnit
import com.patrykandpatryk.liftapp.domain.unit.RatioUnit
import com.patrykandpatryk.liftapp.domain.unit.RepsUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.StateFlow

class Formatter(private val stringProvider: StringProvider, private val is24H: StateFlow<Boolean>) {
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

    fun formatDate(localDateTime: LocalDateTime, dateFormat: DateFormat): String =
        localDateTime.format(DateTimeFormatter.ofPattern(dateFormat.getPattern()))

    fun formatDate(localDate: LocalDate, dateFormat: DateFormat): String =
        localDate.format(DateTimeFormatter.ofPattern(dateFormat.getPattern()))

    private fun DateFormat.getPattern(): String =
        when (this) {
            DateFormat.MinutesSeconds -> MINUTES_SECONDS
            DateFormat.HoursMinutes -> if (is24H()) HOURS_MINUTES_24H else HOURS_MINUTES_12H
            DateFormat.HoursMinutesSeconds ->
                if (is24H()) HOURS_MINUTES_SECONDS_24H else HOURS_MINUTES_SECONDS_12H

            DateFormat.Day -> stringProvider.dateFormatDay
            DateFormat.DayMonth -> stringProvider.dateFormatDayMonth
            DateFormat.WeekdayDayMonth -> stringProvider.dateFormatWeekdayDayMonth
            DateFormat.DayMonthYear -> stringProvider.dateFormatDayMonthYear
            DateFormat.WeekdayDayMonthYear -> stringProvider.dateWeekdayDayMonthYear
            DateFormat.MonthYear -> stringProvider.dateMonthYear
            DateFormat.Year -> stringProvider.dateYear
        }

    fun is24H(): Boolean = is24H.value

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

    fun formatWeight(weight: Double, massUnit: MassUnit): String =
        "${formatNumber(weight, format = NumberFormat.Decimal)}${stringProvider.getDisplayUnit(massUnit)}"

    fun formatValue(value: Number, unit: ValueUnit): String =
        when (unit) {
            is MassUnit,
            is EnergyUnit,
            is ShortDistanceUnit,
            is MediumDistanceUnit,
            is LongDistanceUnit,
            is PercentageUnit ->
                "${formatNumber(value, format = NumberFormat.Decimal)}${stringProvider.getDisplayUnit(unit)}"

            is RepsUnit -> formatReps(value.toInt())
            is DurationUnit -> formatDuration(value.toLong())
            is RatioUnit<*, *> -> formatRatioUnit(value, unit)
            else -> formatNumber(value, format = NumberFormat.Decimal)
        }

    fun formatDuration(value: Long): String = formatDuration(value.milliseconds)

    fun formatDuration(duration: Duration): String {
        val pattern =
            when {
                duration.inWholeHours > 0 -> DateFormat.HoursMinutesSeconds.getPattern()
                else -> DateFormat.MinutesSeconds.getPattern()
            }
        return SimpleDateFormat(pattern, Locale.getDefault()).format(duration.inWholeMilliseconds)
    }

    private fun formatReps(reps: Int): String =
        "${formatNumber(reps, format = NumberFormat.Integer)} ${stringProvider.getRepsString(reps)}"

    private fun formatRatioUnit(value: Number, unit: RatioUnit<*, *>): String =
        "%s/%s"
            .format(
                formatValue(value, unit.dividend),
                stringProvider.getDisplayUnit(unit.divisor, respectLeadingSpaceSetting = false),
            )

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

    fun getLocalTimeFormatter(): DateTimeFormatter =
        DateTimeFormatter.ofPattern(if (is24H()) HOURS_MINUTES_24H else HOURS_MINUTES_12H)

    enum class DateFormat {
        HoursMinutes,
        HoursMinutesSeconds,
        MinutesSeconds,
        Day,
        DayMonth,
        WeekdayDayMonth,
        DayMonthYear,
        WeekdayDayMonthYear,
        MonthYear,
        Year,
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
