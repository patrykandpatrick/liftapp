package com.patrykandpatryk.liftapp.domain.measurement

import com.patrykandpatryk.liftapp.domain.model.Name
import java.util.Date
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {

    fun getAllMeasurements(): Flow<List<MeasurementWithLatestEntry>>

    suspend fun insertMeasurement(
        name: Name,
        type: MeasurementType,
    )

    suspend fun insertMeasurementEntry(
        parentId: Long,
        values: MeasurementValues,
        timestamp: Date,
    )
}
