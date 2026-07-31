package com.patrykandpatrick.liftapp.functionality.database.exercise

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.patrykandpatrick.liftapp.functionality.database.workout.ExerciseSetWithWorkoutDataDto
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
        "SELECT exercise_set.*, workout_start_date, workout_name, " +
            "COALESCE((" +
            "SELECT membership.workout_item_exercise_notes " +
            "FROM exercise_with_workout_item AS membership " +
            "JOIN workout_item AS item ON item.workout_item_id = membership.workout_item_id " +
            "WHERE item.workout_item_workout_id = exercise_set_workout_id " +
            "AND membership.exercise_id = exercise_set_exercise_id LIMIT 1" +
            "), '') AS workout_item_exercise_notes " +
            "FROM exercise_set " +
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

    @Query(
        "SELECT EXISTS(" +
            "SELECT 1 FROM exercise_set WHERE exercise_set_exercise_id = :exerciseID LIMIT 1" +
            ")"
    )
    fun hasExerciseSets(exerciseID: Long): Flow<Boolean>

    @Insert suspend fun insert(exercise: ExerciseEntity): Long

    @Insert suspend fun insert(exercises: List<ExerciseEntity>): List<Long>

    @Update(entity = ExerciseEntity::class) suspend fun update(exercise: ExerciseEntity.Update)

    @Query("DELETE FROM exercise WHERE exercise_id = :exerciseId")
    suspend fun deleteExercise(exerciseId: Long)

    @Query(
        "DELETE FROM routine_item WHERE NOT EXISTS (" +
            "SELECT 1 FROM exercise_with_routine_item AS membership " +
            "WHERE membership.routine_item_id = routine_item.routine_item_id)"
    )
    suspend fun deleteEmptyRoutineItems()

    @Query(
        "DELETE FROM workout_item WHERE NOT EXISTS (" +
            "SELECT 1 FROM exercise_with_workout_item AS membership " +
            "WHERE membership.workout_item_id = workout_item.workout_item_id)"
    )
    suspend fun deleteEmptyWorkoutItems()

    @Query(
        "UPDATE routine_item SET routine_item_type = 'Exercise' " +
            "WHERE routine_item_type = 'Superset' AND (" +
            "SELECT COUNT(*) FROM exercise_with_routine_item AS membership " +
            "WHERE membership.routine_item_id = routine_item.routine_item_id) = 1"
    )
    suspend fun dissolveSingleExerciseRoutineSupersets()

    @Query(
        "UPDATE workout_item SET workout_item_type = 'Exercise', " +
            "workout_item_sets = NULL, workout_item_rest_time_millis = NULL " +
            "WHERE workout_item_type = 'Superset' AND (" +
            "SELECT COUNT(*) FROM exercise_with_workout_item AS membership " +
            "WHERE membership.workout_item_id = workout_item.workout_item_id) = 1"
    )
    suspend fun dissolveSingleExerciseWorkoutSupersets()

    @Query(
        "DELETE FROM superset WHERE superset_routine_item_id IN (" +
            "SELECT routine_item_id FROM routine_item WHERE routine_item_type = 'Exercise')"
    )
    suspend fun deleteDissolvedSupersetConfigs()

    /**
     * Cascading the exercise memberships can leave an empty item or a one-member superset behind.
     * Repair those parent items before Room publishes the transaction's invalidation.
     */
    @Transaction
    suspend fun delete(exerciseId: Long) {
        deleteExercise(exerciseId)
        deleteEmptyRoutineItems()
        deleteEmptyWorkoutItems()
        dissolveSingleExerciseRoutineSupersets()
        dissolveSingleExerciseWorkoutSupersets()
        deleteDissolvedSupersetConfigs()
    }
}
