package com.patrykandpatrick.liftapp.domain.exercise

import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import kotlinx.coroutines.flow.Flow

fun interface GetRoutineExercisesUseCase {
    fun getRoutineExerciseItems(
        exerciseIds: List<Long>,
        ordered: Boolean,
    ): Flow<List<RoutineExerciseItem>>
}

operator fun GetRoutineExercisesUseCase.invoke(exerciseIds: List<Long>, ordered: Boolean) =
    getRoutineExerciseItems(exerciseIds, ordered)
