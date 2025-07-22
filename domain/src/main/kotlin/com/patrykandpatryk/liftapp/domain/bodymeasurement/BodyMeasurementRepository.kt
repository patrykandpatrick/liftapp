package com.patrykandpatryk.liftapp.domain.bodymeasurement

import kotlinx.coroutines.flow.Flow

interface BodyMeasurementRepository {

    fun getBodyMeasurement(id: Long): Flow<BodyMeasurement>

    fun getBodyMeasurementsWithLatestEntries(): Flow<List<BodyMeasurementWithLatestEntry>>

    fun getBodyMeasurementEntries(bodyMeasurementID: Long): Flow<List<BodyMeasurementEntry>>

    suspend fun insertBodyMeasurement(bodyMeasurement: BodyMeasurement.Insert)

    suspend fun insertBodyMeasurements(bodyMeasurements: List<BodyMeasurement.Insert>)

    suspend fun deleteBodyMeasurementEntry(id: Long)
}
