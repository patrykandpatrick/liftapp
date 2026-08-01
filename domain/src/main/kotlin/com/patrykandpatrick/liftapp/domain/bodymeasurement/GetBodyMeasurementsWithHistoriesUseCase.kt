package com.patrykandpatrick.liftapp.domain.bodymeasurement

import kotlinx.coroutines.flow.Flow

fun interface GetBodyMeasurementsWithHistoriesUseCase {
    /**
     * Every body measurement, each with at most [maxEntriesPerMeasurement] of its newest entries.
     * Measurements that have never been logged are included with no entries.
     */
    fun getBodyMeasurementsWithHistories(
        maxEntriesPerMeasurement: Int
    ): Flow<List<BodyMeasurementWithHistory>>
}

operator fun GetBodyMeasurementsWithHistoriesUseCase.invoke(
    maxEntriesPerMeasurement: Int
): Flow<List<BodyMeasurementWithHistory>> =
    getBodyMeasurementsWithHistories(maxEntriesPerMeasurement)
