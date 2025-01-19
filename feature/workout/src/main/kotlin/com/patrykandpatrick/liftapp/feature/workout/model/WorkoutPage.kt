package com.patrykandpatrick.liftapp.feature.workout.model

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import java.time.LocalDate
import java.time.LocalTime

@Stable
sealed class WorkoutPage : Comparable<WorkoutPage> {
    abstract val index: Int

    abstract val primaryAction: Action.Button

    abstract val secondaryAction: Action.Button

    override fun compareTo(other: WorkoutPage): Int = index.compareTo(other.index)

    data class Exercise(val exercise: EditableWorkout.Exercise, override val index: Int) :
        WorkoutPage() {
        override val primaryAction: Action.Button = Action.NextPage
        override val secondaryAction: Action.Button = Action.PreviousPage
    }

    data class Summary(
        val name: TextFieldState<String>,
        val startDate: TextFieldState<LocalDate>,
        val startTime: TextFieldState<LocalTime>,
        val endDate: TextFieldState<LocalDate>,
        val endTime: TextFieldState<LocalTime>,
        val notes: TextFieldState<String>,
        val is24H: Boolean,
        override val index: Int,
    ) : WorkoutPage() {
        override val primaryAction: Action.Button = Action.FinishWorkout
        override val secondaryAction: Action.Button = Action.PreviousPage
    }
}
