package com.patrykandpatryk.liftapp.domain.bodymeasurement

import kotlinx.coroutines.flow.Flow

fun interface GetBodyMeasurementsWithLatestEntriesUseCase {
    fun getBodyMeasurementsWithLatestEntries(): Flow<List<BodyMeasurementWithLatestEntry>>
}

operator fun GetBodyMeasurementsWithLatestEntriesUseCase.invoke():
    Flow<List<BodyMeasurementWithLatestEntry>> = getBodyMeasurementsWithLatestEntries()
