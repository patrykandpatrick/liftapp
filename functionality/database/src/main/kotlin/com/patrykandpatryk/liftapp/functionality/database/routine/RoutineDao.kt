package com.patrykandpatryk.liftapp.functionality.database.routine

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Query("SELECT * FROM routine_with_exercise_names")
    fun getRoutinesWithExerciseNames(): Flow<List<RoutineWithExerciseNamesView>>

    @Transaction
    @Query("SELECT * FROM routine WHERE id = :routineId")
    fun getRoutineWithExercises(routineId: Long): Flow<RoutineWithExercisesRelation?>

    @Query("SELECT * FROM routine WHERE id = :routineId")
    fun getRoutine(routineId: Long): Flow<RoutineEntity?>

    @Upsert
    suspend fun upsert(routine: RoutineEntity): Long

    @Insert
    suspend fun insert(routine: RoutineEntity): Long

    @Insert
    suspend fun insert(exerciseWithRoutine: ExerciseWithRoutineEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exerciseWithRoutine: List<ExerciseWithRoutineEntity>)

    @Query("DELETE FROM exercise_with_routine WHERE  routine_id = :routineId AND exercise_id NOT IN (:notIn)")
    suspend fun deleteExerciseWithRoutinesNotIn(routineId: Long, notIn: List<Long>)

    @Query("DELETE FROM routine WHERE id = :routineId")
    suspend fun delete(routineId: Long)
}
