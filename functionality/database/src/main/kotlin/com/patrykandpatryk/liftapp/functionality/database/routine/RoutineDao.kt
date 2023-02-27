package com.patrykandpatryk.liftapp.functionality.database.routine

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseWithGoalDto
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Query("SELECT * FROM routine_with_exercise_names")
    fun getRoutinesWithExerciseNames(): Flow<List<RoutineWithExerciseNamesView>>

    @Transaction
    @Query(
        value = "SELECT exercise.*, goal.* from exercise_with_routine as ewr " +
            "LEFT JOIN exercise ON exercise.exercise_id = ewr.exercise_id " +
            "LEFT JOIN goal on goal.goal_exercise_id = exercise.exercise_id " +
            "AND goal.goal_routine_id = :routineId " +
            "WHERE ewr.routine_id = :routineId ORDER BY order_index",
    )
    fun getRoutineExercises(routineId: Long): Flow<List<ExerciseWithGoalDto>>

    @Query("SELECT * FROM routine WHERE routine_id = :routineId")
    fun getRoutine(routineId: Long): Flow<RoutineEntity?>

    @Upsert
    suspend fun upsert(routine: RoutineEntity): Long

    @Insert
    suspend fun insert(routine: RoutineEntity): Long

    @Insert
    suspend fun insert(exerciseWithRoutine: ExerciseWithRoutineEntity)

    @Upsert
    suspend fun upsert(exerciseWithRoutine: List<ExerciseWithRoutineEntity>)

    @Query("DELETE FROM exercise_with_routine WHERE routine_id = :routineId AND exercise_id = :exerciseId")
    suspend fun deleteExerciseWithRoutine(routineId: Long, exerciseId: Long)

    @Query("DELETE FROM exercise_with_routine WHERE  routine_id = :routineId AND exercise_id NOT IN (:notIn)")
    suspend fun deleteExerciseWithRoutinesNotIn(routineId: Long, notIn: List<Long>)

    @Query("DELETE FROM routine WHERE routine_id = :routineId")
    suspend fun delete(routineId: Long)
}
