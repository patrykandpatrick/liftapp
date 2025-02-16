package com.patrykandpatryk.liftapp.domain.routine

import kotlinx.coroutines.flow.Flow

interface GetRoutineWithExercisesContract {
    fun getRoutineWithExercises(routineID: Long): Flow<RoutineWithExercises?>
}
