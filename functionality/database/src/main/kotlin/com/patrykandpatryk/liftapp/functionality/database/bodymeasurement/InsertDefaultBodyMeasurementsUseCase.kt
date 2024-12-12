package com.patrykandpatryk.liftapp.functionality.database.bodymeasurement

import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurement
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.functionality.database.string.BodyMeasurementStringResource
import javax.inject.Inject

class InsertDefaultBodyMeasurementsUseCase
@Inject
constructor(private val repository: BodyMeasurementRepository) {

    private val bodyMeasurements: List<BodyMeasurement.Insert> =
        listOf(
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.BodyWeight),
                type = BodyMeasurementType.Weight,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.FatPercentage),
                type = BodyMeasurementType.Percentage,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.MusclePercentage),
                type = BodyMeasurementType.Percentage,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.ForearmCircumference),
                type = BodyMeasurementType.LengthTwoSides,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.BicepCircumference),
                type = BodyMeasurementType.LengthTwoSides,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.ChestCircumference),
                type = BodyMeasurementType.Length,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.AbCircumference),
                type = BodyMeasurementType.Length,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.GluteCircumference),
                type = BodyMeasurementType.Length,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.ThighCircumference),
                type = BodyMeasurementType.LengthTwoSides,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.CalfCircumference),
                type = BodyMeasurementType.LengthTwoSides,
            ),
        )

    suspend operator fun invoke() {
        repository.insertBodyMeasurements(bodyMeasurements)
    }
}
