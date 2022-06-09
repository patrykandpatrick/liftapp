package com.patrykandpatryk.liftapp.domain.measurement

import com.patrykandpatryk.liftapp.domain.model.Name
import javax.inject.Inject

class InsertMeasurementUseCase @Inject constructor(
    private val repository: MeasurementRepository,
) {

    suspend operator fun invoke(
        name: Name,
        type: MeasurementType,
    ) {
        repository.insertMeasurement(
            name = name,
            type = type,
        )
    }
}
