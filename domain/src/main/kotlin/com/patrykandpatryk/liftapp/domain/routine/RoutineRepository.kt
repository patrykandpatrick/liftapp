package com.patrykandpatryk.liftapp.domain.routine

import kotlinx.coroutines.flow.Flow

interface RoutineRepository {

    fun getRoutinesWithNames(): Flow<List<RoutineWithExerciseNames>>

    fun getRoutineWithExercises(routineId: Long): Flow<RoutineWithExercises?>

    suspend fun upsert(routine: Routine, exerciseIds: List<Long>): Long

    suspend fun reorderExercises(routineId: Long, exerciseIds: List<Long>)

    suspend fun delete(routineId: Long)

    suspend fun deleteExerciseWithRoutine(routineId: Long, exerciseId: Long)
}
