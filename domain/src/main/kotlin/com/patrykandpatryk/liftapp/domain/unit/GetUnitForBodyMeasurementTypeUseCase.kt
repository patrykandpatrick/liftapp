package com.patrykandpatryk.liftapp.domain.unit

import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUnitForBodyMeasurementTypeUseCase @Inject constructor(
    private val preferences: PreferenceRepository,
) {
    suspend operator fun invoke(bodyMeasurementType: BodyMeasurementType) =
        when (bodyMeasurementType) {
            BodyMeasurementType.Weight,
            -> preferences.massUnit.get().first()

            BodyMeasurementType.Length, BodyMeasurementType.LengthTwoSides,
            -> preferences.shortDistanceUnit.first()

            BodyMeasurementType.Percentage,
            -> PercentageUnit
        }
}
