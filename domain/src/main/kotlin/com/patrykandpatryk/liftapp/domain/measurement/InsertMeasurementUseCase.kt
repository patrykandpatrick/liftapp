package com.patrykandpatryk.liftapp.domain.measurement

import javax.inject.Inject

class InsertMeasurementUseCase @Inject constructor(
    private val repository: MeasurementRepository,
) {

    suspend operator fun invoke(measurement: Measurement.Insert) {
        repository.insertMeasurement(measurement)
    }
}
