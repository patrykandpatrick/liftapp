package com.patrykandpatryk.liftapp.functionality.database.exercise

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.patrykandpatryk.liftapp.functionality.database.workout.ExerciseSetWithWorkoutDataDto
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercise") fun getAllExercises(): Flow<List<ExerciseEntity>>

    @RawQuery(observedEntities = [ExerciseEntity::class])
    fun getExercises(query: SupportSQLiteQuery): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercise WHERE exercise_id = :id")
    fun getExercise(id: Long): Flow<ExerciseEntity?>

    @Query("SELECT exercise_name, exercise_type FROM exercise WHERE exercise_id = :id")
    fun getExerciseNameAndType(id: Long): Flow<ExerciseNameAndTypeDto?>

    @Query(
        "SELECT exercise_set.*, workout_start_date, workout_name FROM exercise_set " +
            "LEFT JOIN workout on workout_id = exercise_set_workout_id " +
            "WHERE exercise_set_exercise_id = :exerciseID " +
            "AND workout_start_date >= :startDateTime AND workout_start_date < :endDateTime " +
            "ORDER BY workout_start_date DESC"
    )
    fun getExerciseSets(
        exerciseID: Long,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ): Flow<List<ExerciseSetWithWorkoutDataDto>>

    @Insert suspend fun insert(exercise: ExerciseEntity): Long

    @Insert suspend fun insert(exercises: List<ExerciseEntity>): List<Long>

    @Update(entity = ExerciseEntity::class) suspend fun update(exercise: ExerciseEntity.Update)

    @Query("DELETE FROM exercise WHERE exercise_id = :exerciseId")
    suspend fun delete(exerciseId: Long)
}
