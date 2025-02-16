package com.patrykandpatryk.liftapp.feature.newroutine.model

import com.patrykandpatryk.liftapp.domain.exercise.GetRoutineExercisesContract
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class GetExerciseItemsUseCase
@Inject
constructor(
    private val newRoutineSavedState: NewRoutineSavedState,
    private val getRoutineExercisesContract: GetRoutineExercisesContract,
) {
    operator fun invoke(): Flow<List<RoutineExerciseItem>> =
        newRoutineSavedState.exerciseIDs.flatMapLatest { ids ->
            if (ids.isNotEmpty()) {
                getRoutineExercisesContract.getRoutineExerciseItems(ids, true)
            } else {
                flowOf(emptyList())
            }
        }
}
