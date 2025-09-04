package com.patrykandpatryk.liftapp.domain.unit

import com.patrykandpatryk.liftapp.domain.extension.getTypeErrorMessage
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class UnitConverter
@Inject
constructor(
    private val formatter: Formatter,
    private val stringProvider: StringProvider,
    private val preferences: PreferenceRepository,
) {

    fun convert(from: MassUnit, to: MassUnit, value: Double): Double =
        when (to) {
            MassUnit.Kilograms -> from.toKilograms(value)
            MassUnit.Pounds -> from.toPounds(value)
        }

    suspend fun convertToPreferredUnit(from: LongDistanceUnit, value: Double): Double =
        when (getPreferredLongDistanceUnit()) {
            LongDistanceUnit.Kilometer -> from.toKilometers(value)
            LongDistanceUnit.Mile -> from.toMiles(value)
        }

    suspend fun convertToPreferredUnit(from: MediumDistanceUnit, value: Double): Double =
        when (getPreferredMediumDistanceUnit()) {
            MediumDistanceUnit.Meter -> from.toMeters(value)
            MediumDistanceUnit.Foot -> from.toFeet(value)
        }

    suspend fun convertToPreferredUnit(from: ShortDistanceUnit, value: Double): Double =
        when (getPreferredShortDistanceUnit()) {
            ShortDistanceUnit.Centimeter -> from.toCentimeters(value)
            ShortDistanceUnit.Inch -> from.toInch(value)
        }

    suspend fun convertToPreferredUnit(from: MassUnit, value: Double): Double =
        convert(from, getPreferredMassUnit(), value)

    suspend fun convertToPreferredUnit(from: ValueUnit, value: Double): Double =
        when (from) {
            is MassUnit -> convertToPreferredUnit(from = from, value = value)
            is LongDistanceUnit -> convertToPreferredUnit(from = from, value = value)
            is MediumDistanceUnit -> convertToPreferredUnit(from = from, value = value)
            is ShortDistanceUnit -> convertToPreferredUnit(from = from, value = value)
            is PercentageUnit -> value
            is EnergyUnit -> value
            else -> error(getTypeErrorMessage(unit = from))
        }

    suspend fun convertToPreferredUnitAndFormat(from: ValueUnit, vararg values: Double): String {
        val convertedValues = values.map { convertToPreferredUnit(from, it) }.toTypedArray()

        val postfix =
            when (from) {
                is MassUnit -> stringProvider.getDisplayUnit(getPreferredMassUnit())
                is LongDistanceUnit -> stringProvider.getDisplayUnit(getPreferredLongDistanceUnit())
                is MediumDistanceUnit ->
                    stringProvider.getDisplayUnit(getPreferredMediumDistanceUnit())
                is ShortDistanceUnit ->
                    stringProvider.getDisplayUnit(getPreferredShortDistanceUnit())
                is PercentageUnit -> stringProvider.getDisplayUnit(PercentageUnit)
                else -> error(getTypeErrorMessage(unit = from))
            }

        return formatter.formatNumber(
            *convertedValues,
            format = Formatter.NumberFormat.Decimal,
            postfix = postfix,
        )
    }

    private suspend fun getPreferredLongDistanceUnit(): LongDistanceUnit =
        preferences.longDistanceUnit.get().first()

    private suspend fun getPreferredMediumDistanceUnit(): MediumDistanceUnit =
        preferences.mediumDistanceUnit.first()

    private suspend fun getPreferredShortDistanceUnit(): ShortDistanceUnit =
        preferences.shortDistanceUnit.first()

    private suspend fun getPreferredMassUnit(): MassUnit = preferences.massUnit.get().first()
}
