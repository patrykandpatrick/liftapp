package com.patrykandpatryk.liftapp.functionality.database.measurement

import com.patrykandpatryk.liftapp.domain.measurement.Measurement
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementRepository
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementType
import com.patrykandpatryk.liftapp.domain.model.Name
import javax.inject.Inject

class InsertDefaultMeasurements @Inject constructor(
    private val repository: MeasurementRepository,
) {

    private val measurements: List<Measurement.Insert> = listOf(
        Measurement.Insert(
            name = Name.Resource("measurement_body_weight"),
            type = MeasurementType.Weight,
        ),
        Measurement.Insert(
            name = Name.Resource("measurement_fat_percentage"),
            type = MeasurementType.Percentage,
        ),
        Measurement.Insert(
            name = Name.Resource("measurement_muscle_percentage"),
            type = MeasurementType.Percentage,
        ),
        Measurement.Insert(
            name = Name.Resource("measurement_forearm_circumference"),
            type = MeasurementType.LengthTwoSides,
        ),
        Measurement.Insert(
            name = Name.Resource("measurement_bicep_circumference"),
            type = MeasurementType.LengthTwoSides,
        ),
        Measurement.Insert(
            name = Name.Resource("measurement_chest_circumference"),
            type = MeasurementType.Length,
        ),
        Measurement.Insert(
            name = Name.Resource("measurement_ab_circumference"),
            type = MeasurementType.Length,
        ),
        Measurement.Insert(
            name = Name.Resource("measurement_glute_circumference"),
            type = MeasurementType.Length,
        ),
        Measurement.Insert(
            name = Name.Resource("measurement_thigh_circumference"),
            type = MeasurementType.LengthTwoSides,
        ),
        Measurement.Insert(
            name = Name.Resource("measurement_calf_circumference"),
            type = MeasurementType.LengthTwoSides,
        ),
    )

    suspend operator fun invoke() {
        repository.insertMeasurements(measurements)
    }
}
