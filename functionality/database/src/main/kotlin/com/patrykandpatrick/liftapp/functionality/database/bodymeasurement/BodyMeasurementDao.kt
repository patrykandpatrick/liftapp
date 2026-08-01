package com.patrykandpatrick.liftapp.functionality.database.bodymeasurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {

    @Query("SELECT * FROM body_measurements WHERE id = :id")
    fun getBodyMeasurement(id: Long): Flow<BodyMeasurementEntity>

    @Query("SELECT * FROM body_measurements ORDER BY id")
    fun getBodyMeasurements(): Flow<List<BodyMeasurementEntity>>

    /**
     * The newest [maxEntriesPerMeasurement] entries of every body measurement, newest first. The
     * per-measurement cap is expressed as "how many entries are newer than this one" because SQLite
     * has no per-group LIMIT.
     *
     * Newer means later, and where two entries share a time, the one entered second. Times are kept
     * to the minute, so two readings logged within one minute of each other are equal on time
     * alone; counting only what is strictly later would then let a whole tied group through the
     * cap, returning more entries than were asked for.
     */
    @Query(
        "SELECT * FROM body_measurement_entries AS entry " +
            "WHERE (SELECT COUNT(*) FROM body_measurement_entries AS newer " +
            "WHERE newer.body_measurement_id = entry.body_measurement_id " +
            "AND (newer.time > entry.time " +
            "OR newer.time = entry.time AND newer.id > entry.id)) " +
            "< :maxEntriesPerMeasurement " +
            "ORDER BY entry.time DESC, entry.id DESC"
    )
    fun getRecentBodyMeasurementEntries(
        maxEntriesPerMeasurement: Int
    ): Flow<List<BodyMeasurementEntryEntity>>

    @Query("SELECT * FROM body_measurements_with_latest_entries WHERE id = :id")
    fun getBodyMeasurementWithLatestEntry(id: Long): Flow<BodyMeasurementWithLatestEntryViewResult>

    @Query("SELECT * FROM body_measurements_with_latest_entries")
    fun getBodyMeasurementsWithLatestEntries(): Flow<List<BodyMeasurementWithLatestEntryViewResult>>

    @Query(
        "SELECT * FROM body_measurement_entries WHERE body_measurement_id = :bodyMeasurementID " +
            "AND time >= :startDateTime AND time < :endDateTime " +
            "UNION SELECT * FROM body_measurement_entries WHERE body_measurement_id = :bodyMeasurementID AND time =" +
            "(SELECT MAX(time) FROM BODY_MEASUREMENT_ENTRIES WHERE body_measurement_id = :bodyMeasurementID AND " +
            "time < :startDateTime) " +
            "UNION SELECT * FROM body_measurement_entries WHERE body_measurement_id = :bodyMeasurementID AND time =" +
            "(SELECT MIN(time) FROM BODY_MEASUREMENT_ENTRIES WHERE body_measurement_id = :bodyMeasurementID AND " +
            "time > :endDateTime) " +
            "ORDER BY time DESC"
    )
    fun getBodyMeasurementEntries(
        bodyMeasurementID: Long,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ): Flow<List<BodyMeasurementEntryEntity>>

    @Query("SELECT * FROM body_measurement_entries WHERE id = :id LIMIT 1")
    fun getBodyMeasurementEntry(id: Long): Flow<BodyMeasurementEntryEntity?>

    @Insert suspend fun insertBodyMeasurement(bodyMeasurement: BodyMeasurementEntity)

    @Upsert suspend fun insertBodyMeasurementEntry(bodyMeasurementEntry: BodyMeasurementEntryEntity)

    @Update suspend fun updateBodyMeasurementEntry(bodyMeasurementEntry: BodyMeasurementEntryEntity)

    @Query("DELETE FROM body_measurement_entries WHERE id = :id")
    suspend fun deleteBodyMeasurementEntry(id: Long)
}
