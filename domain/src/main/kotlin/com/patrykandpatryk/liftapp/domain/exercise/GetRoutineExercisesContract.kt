package com.patrykandpatryk.liftapp.domain.exercise

import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import kotlinx.coroutines.flow.Flow

fun interface GetRoutineExercisesContract {
    fun getRoutineExerciseItems(ids: List<Long>, ordered: Boolean): Flow<List<RoutineExerciseItem>>
}
