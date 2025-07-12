package com.patrykandpatryk.liftapp.feature.newroutine.model

import com.patrykandpatryk.liftapp.feature.newroutine.ui.NewRoutineState
import kotlin.coroutines.Continuation

internal sealed interface Action {
    class AddExercises(val ids: List<Long>) : Action

    class RemoveExercise(val id: Long) : Action

    class SaveRoutine(val state: NewRoutineState) : Action

    class ReorderExercise(
        val fromIndex: Int,
        val toIndex: Int,
        val continuation: Continuation<Unit>,
    ) : Action

    data class DeleteRoutine(val id: Long) : Action

    data object PopBackStack : Action

    data class PickExercises(val disabledExerciseIDs: List<Long>) : Action
}
