package com.patrykandpatrick.liftapp.domain.routine

import kotlinx.coroutines.flow.Flow

fun interface GetRoutineWithExercisesUseCase {
    fun getRoutineWithExercises(routineId: Long): Flow<RoutineWithExercises?>
}

operator fun GetRoutineWithExercisesUseCase.invoke(routineId: Long): Flow<RoutineWithExercises?> =
    getRoutineWithExercises(routineId)
