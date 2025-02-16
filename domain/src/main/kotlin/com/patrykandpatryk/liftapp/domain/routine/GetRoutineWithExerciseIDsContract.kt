package com.patrykandpatryk.liftapp.domain.routine

import kotlinx.coroutines.flow.Flow

fun interface GetRoutineWithExerciseIDsContract {
    fun getRoutineWithExerciseIDs(routineID: Long): Flow<RoutineWithExerciseIds?>
}
