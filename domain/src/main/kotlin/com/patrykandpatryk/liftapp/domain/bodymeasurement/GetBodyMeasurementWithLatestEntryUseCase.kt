package com.patrykandpatryk.liftapp.domain.bodymeasurement

import kotlinx.coroutines.flow.Flow

fun interface GetBodyMeasurementWithLatestEntryUseCase {
    fun getBodyMeasurementWithLatestEntry(id: Long): Flow<BodyMeasurementWithLatestEntry>
}

operator fun GetBodyMeasurementWithLatestEntryUseCase.invoke(
    id: Long
): Flow<BodyMeasurementWithLatestEntry> = getBodyMeasurementWithLatestEntry(id)
