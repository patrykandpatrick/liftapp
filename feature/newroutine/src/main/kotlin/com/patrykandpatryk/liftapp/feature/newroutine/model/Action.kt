package com.patrykandpatryk.liftapp.feature.newroutine.model

import com.patrykandpatryk.liftapp.feature.newroutine.ui.NewRoutineState

internal sealed interface Action {
    class AddExercises(val ids: List<Long>) : Action

    class RemoveExercise(val id: Long) : Action

    class SaveRoutine(val state: NewRoutineState) : Action
}
