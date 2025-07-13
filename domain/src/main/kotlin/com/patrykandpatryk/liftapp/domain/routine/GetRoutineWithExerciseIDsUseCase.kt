package com.patrykandpatryk.liftapp.domain.routine

import kotlinx.coroutines.flow.Flow

fun interface GetRoutineWithExerciseIDsUseCase {
    fun getRoutineWithExerciseIDs(routineID: Long): Flow<RoutineWithExerciseIds?>
}

operator fun GetRoutineWithExerciseIDsUseCase.invoke(routineID: Long) =
    getRoutineWithExerciseIDs(routineID)
