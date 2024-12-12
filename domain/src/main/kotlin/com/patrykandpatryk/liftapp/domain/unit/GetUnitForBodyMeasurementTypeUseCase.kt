package com.patrykandpatryk.liftapp.domain.unit

import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class GetUnitForBodyMeasurementTypeUseCase
@Inject
constructor(private val preferences: PreferenceRepository) {
    suspend operator fun invoke(bodyMeasurementType: BodyMeasurementType) =
        when (bodyMeasurementType) {
            BodyMeasurementType.Weight -> preferences.massUnit.get().first()

            BodyMeasurementType.Length,
            BodyMeasurementType.LengthTwoSides -> preferences.shortDistanceUnit.first()

            BodyMeasurementType.Percentage -> PercentageUnit
        }
}
