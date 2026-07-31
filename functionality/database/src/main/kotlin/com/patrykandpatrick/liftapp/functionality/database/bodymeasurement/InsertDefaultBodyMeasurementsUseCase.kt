package com.patrykandpatrick.liftapp.functionality.database.bodymeasurement

import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import javax.inject.Inject

class InsertDefaultBodyMeasurementsUseCase
@Inject
constructor(private val repository: BodyMeasurementRepository) {

    suspend operator fun invoke() {
        repository.insertBodyMeasurements(DefaultBodyMeasurements.bodyMeasurements)
    }
}
