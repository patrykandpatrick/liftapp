package com.patrykandpatryk.liftapp.domain.bodymeasurement

import javax.inject.Inject

class InsertBodyMeasurementUseCase @Inject constructor(
    private val repository: BodyMeasurementRepository,
) {

    suspend operator fun invoke(bodyMeasurement: BodyMeasurement.Insert) {
        repository.insertBodyMeasurement(bodyMeasurement)
    }
}
