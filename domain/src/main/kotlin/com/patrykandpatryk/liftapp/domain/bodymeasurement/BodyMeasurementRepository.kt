package com.patrykandpatryk.liftapp.domain.bodymeasurement

import kotlinx.coroutines.flow.Flow

interface BodyMeasurementRepository {

    fun getBodyMeasurement(id: Long): Flow<BodyMeasurement>

    fun getBodyMeasurementWithLatestEntry(id: Long): Flow<BodyMeasurementWithLatestEntry>

    fun getBodyMeasurementsWithLatestEntries(): Flow<List<BodyMeasurementWithLatestEntry>>

    fun getBodyMeasurementEntries(bodyMeasurementID: Long): Flow<List<BodyMeasurementEntry>>

    suspend fun getBodyMeasurementEntry(id: Long): BodyMeasurementEntry?

    suspend fun insertBodyMeasurement(bodyMeasurement: BodyMeasurement.Insert)

    suspend fun insertBodyMeasurements(bodyMeasurements: List<BodyMeasurement.Insert>)

    suspend fun insertBodyMeasurementEntry(
        bodyMeasurementID: Long,
        value: BodyMeasurementValue,
        timestamp: Long,
    )

    suspend fun updateBodyMeasurementEntry(
        id: Long,
        bodyMeasurementID: Long,
        value: BodyMeasurementValue,
        timestamp: Long,
    )
}
