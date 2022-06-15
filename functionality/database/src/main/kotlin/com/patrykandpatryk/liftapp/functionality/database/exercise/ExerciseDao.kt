package com.patrykandpatryk.liftapp.functionality.database.exercise

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercise")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Insert
    suspend fun insert(exercise: ExerciseEntity): Long

    @Insert
    suspend fun insert(exercises: List<ExerciseEntity>): List<Long>

    @Update(entity = ExerciseEntity::class)
    suspend fun update(exercise: ExerciseEntity.Update)

    @Query("DELETE FROM exercise WHERE id = :exerciseId")
    suspend fun delete(exerciseId: Long)
}
