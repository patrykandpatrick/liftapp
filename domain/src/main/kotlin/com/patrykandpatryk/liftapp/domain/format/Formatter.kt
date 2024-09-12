package com.patrykandpatryk.liftapp.domain.format

import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class Formatter(
    private val stringProvider: StringProvider,
    private val is24H: Flow<Boolean>,
) {
    @Inject
    constructor(
        stringProvider: StringProvider,
        preferenceRepository: PreferenceRepository,
    ) : this(stringProvider, preferenceRepository.is24H)

    private val decimalSymbols = DecimalFormatSymbols(Locale.getDefault())

    private val decimalOutputFormat = DecimalFormat("#,###.##", decimalSymbols)

    private val decimalInputFormat = DecimalFormat("#.##", decimalSymbols)

    private val integerOutputFormat = DecimalFormat("#,###", decimalSymbols)

    private val percentFormat = DecimalFormat("#%")

    private val decimalNumberRegex = """\d+${decimalSymbols.decimalSeparator}?\d*""".toRegex()

    fun formatDate(localDateTime: LocalDateTime, dateFormat: DateFormat): String =
        localDateTime.format(DateTimeFormatter.ofPattern(dateFormat.getPattern()))

    private fun DateFormat.getPattern(): String = when (this) {
        DateFormat.TimeShort -> stringProvider.timeFormatShort24h
        DateFormat.TimeLong -> stringProvider.timeFormatLong24h
        DateFormat.DateShort -> stringProvider.dateFormatShort
        DateFormat.DateLong -> stringProvider.dateFormatLong
        DateFormat.DateFull -> stringProvider.dateFormatFull
        DateFormat.DateEdit -> stringProvider.dateFormatEdit
    }

    fun getFormattedDate(localDateTime: LocalDateTime): FormattedDate =
        FormattedDate(
            formatDate(localDateTime, DateFormat.DateShort),
            formatDate(localDateTime, DateFormat.DateLong),
            formatDate(localDateTime, DateFormat.TimeShort),
            formatDate(localDateTime, DateFormat.TimeLong),
            localDateTime,
        )

    suspend fun is24H(): Boolean = is24H.first()

    fun formatNumber(
        vararg numbers: Number,
        format: NumberFormat,
        separator: String = " | ",
        prefix: String? = null,
        postfix: String? = null,
    ): String = numbers.joinToString(
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
        "${formatNumber(weight, format = NumberFormat.Decimal) } ${stringProvider.getDisplayUnit(massUnit)}"

    fun toFloatOrZero(value: String): Float =
        try {
            decimalInputFormat.parse(value)?.toFloat()
        } catch (exception: ParseException) {
            null
        } ?: 0f

    fun toInputDecimalNumber(value: Float): String = decimalInputFormat.format(value)

    fun isValidNumber(value: String): Boolean = decimalNumberRegex.matches(value)

    fun round(value: Double): Double =
        decimalInputFormat.parse(decimalInputFormat.format(value)).toDouble()

    enum class DateFormat {
        TimeShort, TimeLong, DateShort, DateLong, DateFull, DateEdit
    }

    enum class NumberFormat {
        Decimal,
        Integer,
    }
}
