package com.patrykandpatryk.liftapp.domain.measurement

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetMeasurementsWithLatestEntriesUseCase @Inject constructor(
    private val repository: MeasurementRepository,
) {

    operator fun invoke(): Flow<List<MeasurementWithLatestEntry>> =
        repository.getAllMeasurements()
}
