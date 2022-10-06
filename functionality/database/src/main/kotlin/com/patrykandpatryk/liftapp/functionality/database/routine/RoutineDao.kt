package com.patrykandpatryk.liftapp.functionality.database.routine

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Query("SELECT * FROM routine_with_exercise_names")
    fun getRoutinesWithExerciseNames(): Flow<List<RoutineWithExerciseNamesView>>

    @Query("SELECT * FROM routine WHERE id = :routineId")
    fun getRoutineWithExercises(routineId: Long): Flow<RoutineWithExercisesRelation?>

    @Insert
    suspend fun insert(routine: RoutineEntity): Long

    @Insert
    suspend fun insert(exerciseWithRoutine: ExerciseWithRoutineEntity)
}
