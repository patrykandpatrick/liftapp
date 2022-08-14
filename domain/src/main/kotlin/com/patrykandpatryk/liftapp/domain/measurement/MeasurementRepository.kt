package com.patrykandpatryk.liftapp.domain.measurement

import java.util.Calendar
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {

    fun getMeasurement(id: Long): Flow<Measurement>

    fun getAllMeasurements(): Flow<List<MeasurementWithLatestEntry>>

    suspend fun insertMeasurement(measurement: Measurement.Insert)

    suspend fun insertMeasurements(measurements: List<Measurement.Insert>)

    suspend fun insertMeasurementEntry(
        parentId: Long,
        values: MeasurementValues,
        timestamp: Calendar,
    )
}
