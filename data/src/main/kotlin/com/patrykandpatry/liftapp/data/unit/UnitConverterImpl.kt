package com.patrykandpatry.liftapp.data.unit

import com.patrykandpatryk.liftapp.domain.extension.getTypeErrorMessage
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.PercentageUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UnitConverterImpl @Inject constructor(
    private val formatter: Formatter,
    private val stringProvider: StringProvider,
    private val preferences: PreferenceRepository,
) : UnitConverter {

    override fun convert(from: MassUnit, to: MassUnit, value: Double): Double =
        when (to) {
            MassUnit.Kilograms -> from.toKilograms(value)
            MassUnit.Pounds -> from.toPounds(value)
        }

    override suspend fun convertToPreferredUnit(from: LongDistanceUnit, value: Double): Double =
        when (getPreferredLongDistanceUnit()) {
            LongDistanceUnit.Kilometer -> from.toKilometers(value)
            LongDistanceUnit.Mile -> from.toMiles(value)
        }

    override suspend fun convertToPreferredUnit(from: MediumDistanceUnit, value: Double): Double =
        when (getPreferredMediumDistanceUnit()) {
            MediumDistanceUnit.Meter -> from.toMeters(value)
            MediumDistanceUnit.Foot -> from.toFeet(value)
        }

    override suspend fun convertToPreferredUnit(from: ShortDistanceUnit, value: Double): Double =
        when (getPreferredShortDistanceUnit()) {
            ShortDistanceUnit.Centimeter -> from.toCentimeters(value)
            ShortDistanceUnit.Inch -> from.toInch(value)
            else -> error(getTypeErrorMessage(unit = from))
        }

    override suspend fun convertToPreferredUnit(from: MassUnit, value: Double): Double =
        convert(from, getPreferredMassUnit(), value)

    override suspend fun convertToPreferredUnitAndFormat(from: ValueUnit, vararg values: Double): String {
        val convertedValues = when (from) {
            is MassUnit -> values.map { convertToPreferredUnit(from = from, value = it) }.toTypedArray()
            is LongDistanceUnit -> values.map { convertToPreferredUnit(from = from, value = it) }.toTypedArray()
            is MediumDistanceUnit -> values.map { convertToPreferredUnit(from = from, value = it) }.toTypedArray()
            is ShortDistanceUnit -> values.map { convertToPreferredUnit(from = from, value = it) }.toTypedArray()
            is PercentageUnit -> values.toTypedArray()
            else -> error(getTypeErrorMessage(unit = from))
        }

        val postfix = when (from) {
            is MassUnit -> stringProvider.getDisplayUnit(getPreferredMassUnit())
            is LongDistanceUnit -> stringProvider.getDisplayUnit(getPreferredLongDistanceUnit())
            is MediumDistanceUnit -> stringProvider.getDisplayUnit(getPreferredMediumDistanceUnit())
            is ShortDistanceUnit -> stringProvider.getDisplayUnit(getPreferredShortDistanceUnit())
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

    private suspend fun getPreferredMassUnit(): MassUnit =
        preferences.massUnit.get().first()
}
