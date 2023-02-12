package com.patrykandpatryk.liftapp.functionality.database.bodymeasurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {

    @Query("SELECT * FROM body_measurements WHERE id = :id")
    fun getBodyMeasurement(id: Long): Flow<BodyMeasurementEntity>

    @Query("SELECT * FROM body_measurements_with_latest_entries WHERE id = :id")
    fun getBodyMeasurementWithLatestEntry(id: Long): Flow<BodyMeasurementWithLatestEntryViewResult>

    @Query("SELECT * FROM body_measurements_with_latest_entries")
    fun getBodyMeasurementsWithLatestEntries(): Flow<List<BodyMeasurementWithLatestEntryViewResult>>

    @Query(
        "SELECT * FROM body_measurement_entries WHERE body_measurement_id = :bodyMeasurementID ORDER BY timestamp DESC",
    )
    fun getBodyMeasurementEntries(bodyMeasurementID: Long): Flow<List<BodyMeasurementEntryEntity>>

    @Query("SELECT * FROM body_measurement_entries WHERE id = :id LIMIT 1")
    suspend fun getBodyMeasurementEntry(id: Long): BodyMeasurementEntryEntity?

    @Insert
    suspend fun insertBodyMeasurement(bodyMeasurement: BodyMeasurementEntity)

    @Insert
    suspend fun insertBodyMeasurementEntry(bodyMeasurementEntry: BodyMeasurementEntryEntity)

    @Update
    suspend fun updateBodyMeasurementEntry(bodyMeasurementEntry: BodyMeasurementEntryEntity)

    @Query("DELETE FROM body_measurement_entries WHERE id = :id")
    suspend fun deleteBodyMeasurementEntry(id: Long)
}
