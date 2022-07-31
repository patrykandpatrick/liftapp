package com.patrykandpatryk.liftapp.functionality.database.measurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM measurement WHERE id = :id")
    fun getMeasurement(id: Long): Flow<MeasurementEntity>

    @Query("SELECT * FROM measurement")
    fun getMeasurements(): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurement_with_latest_entry")
    fun getMeasurementsWithLatestEntries(): Flow<List<MeasurementWithLatestEntryView>>

    @Insert
    suspend fun insert(measurement: MeasurementEntity)

    @Insert
    suspend fun insert(measurementEntry: MeasurementEntryEntity)
}
