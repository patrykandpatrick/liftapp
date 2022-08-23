package com.patrykandpatry.liftapp.data.unit

import com.patrykandpatryk.liftapp.domain.repository.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UnitConverterImpl @Inject constructor(
    private val preferences: PreferenceRepository,
) : UnitConverter {

    override suspend fun convertToPreferredUnit(from: DistanceUnit, value: Float): Float =
        when (getPreferredDistanceUnit()) {
            DistanceUnit.Kilometers -> from.toKilometers(value)
            DistanceUnit.Miles -> from.toMiles(value)
        }

    override suspend fun convertToPreferredUnit(from: MassUnit, value: Float): Float =
        when (getPreferredMassUnit()) {
            MassUnit.Kilograms -> from.toKilograms(value)
            MassUnit.Pounds -> from.toPounds(value)
        }

    private suspend fun getPreferredDistanceUnit(): DistanceUnit =
        preferences.distanceUnit.get().first()

    private suspend fun getPreferredMassUnit(): MassUnit =
        preferences.massUnit.get().first()
}
