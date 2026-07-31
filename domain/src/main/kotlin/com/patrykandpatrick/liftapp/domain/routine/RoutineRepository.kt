package com.patrykandpatrick.liftapp.domain.routine

import kotlinx.coroutines.flow.Flow

interface RoutineRepository {

    fun getRoutineWithExercises(routineId: Long): Flow<RoutineWithExercises?>

    suspend fun upsert(routine: Routine): Long

    suspend fun upsert(routine: Routine, items: List<RoutineItem>): Long

    suspend fun delete(routineId: Long)
}
