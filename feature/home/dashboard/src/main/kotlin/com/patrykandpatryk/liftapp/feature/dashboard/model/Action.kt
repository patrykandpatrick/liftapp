package com.patrykandpatryk.liftapp.feature.dashboard.model

import java.time.LocalDate

sealed class Action {
    data class NewWorkout(val routineID: Long) : Action()

    data class GoToWorkout(val workoutID: Long) : Action()

    data class GoToRoutine(val routineID: Long) : Action()

    data class SelectDate(val date: LocalDate) : Action()
}
