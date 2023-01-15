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

    @Query("SELECT * FROM exercise WHERE exercise_id in (:ids)")
    fun getExercises(ids: List<Long>): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercise WHERE exercise_id = :id")
    fun getExercise(id: Long): Flow<ExerciseEntity?>

    @Insert
    suspend fun insert(exercise: ExerciseEntity): Long

    @Insert
    suspend fun insert(exercises: List<ExerciseEntity>): List<Long>

    @Update(entity = ExerciseEntity::class)
    suspend fun update(exercise: ExerciseEntity.Update)

    @Query("DELETE FROM exercise WHERE exercise_id = :exerciseId")
    suspend fun delete(exerciseId: Long)
}
