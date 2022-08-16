package com.patrykandpatryk.liftapp.functionality.database.body

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyDao {

    @Query("SELECT * FROM body WHERE id = :id")
    fun getBody(id: Long): Flow<BodyEntity>

    @Query("SELECT * FROM body")
    fun getBodies(): Flow<List<BodyEntity>>

    @Query("SELECT * FROM body_with_latest_entry")
    fun getBodiesWithLatestEntries(): Flow<List<BodyWithLatestEntryView>>

    @Query("SELECT * FROM body_entry WHERE parent_id = :bodyId ORDER BY timestamp DESC")
    fun getBodyEntries(bodyId: Long): Flow<List<BodyEntryEntity>>

    @Insert
    suspend fun insert(body: BodyEntity)

    @Insert
    suspend fun insert(bodyEntry: BodyEntryEntity)
}
