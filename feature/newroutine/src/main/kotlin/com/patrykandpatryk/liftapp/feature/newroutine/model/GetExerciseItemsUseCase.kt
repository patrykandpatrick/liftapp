package com.patrykandpatryk.liftapp.feature.newroutine.model

import com.patrykandpatryk.liftapp.domain.exercise.GetRoutineExercisesUseCase
import com.patrykandpatryk.liftapp.domain.exercise.invoke
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class GetExerciseItemsUseCase
@Inject
constructor(
    private val newRoutineSavedState: NewRoutineSavedState,
    private val getRoutineExercisesUseCase: GetRoutineExercisesUseCase,
) {
    operator fun invoke(): Flow<List<RoutineExerciseItem>> =
        newRoutineSavedState.exerciseIDs.flatMapLatest { ids ->
            if (ids.isNotEmpty()) {
                getRoutineExercisesUseCase(ids, true)
            } else {
                flowOf(emptyList())
            }
        }
}
