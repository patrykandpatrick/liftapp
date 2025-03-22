package com.patrykandpatryk.liftapp.domain.routine

import kotlinx.coroutines.flow.Flow

interface GetRoutinesWithExerciseNamesContract {

    fun getRoutinesWithExerciseNames(): Flow<List<RoutineWithExerciseNames>>
}
