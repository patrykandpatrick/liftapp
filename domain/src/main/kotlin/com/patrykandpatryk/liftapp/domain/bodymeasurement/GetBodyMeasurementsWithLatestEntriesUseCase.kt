package com.patrykandpatryk.liftapp.domain.bodymeasurement

import javax.inject.Inject

class GetBodyMeasurementsWithLatestEntriesUseCase @Inject constructor(
    private val bodyMeasurementRepository: BodyMeasurementRepository,
) {
    operator fun invoke() = bodyMeasurementRepository.getBodyMeasurementsWithLatestEntries()
}
