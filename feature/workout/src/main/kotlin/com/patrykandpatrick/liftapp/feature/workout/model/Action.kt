package com.patrykandpatrick.liftapp.feature.workout.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import java.time.LocalDate
import java.time.LocalTime

sealed interface Action {
    sealed interface Button : Action

    sealed class MovePageBy(val delta: Int) : Button

    data object NextPage : MovePageBy(1)

    data object PreviousPage : MovePageBy(-1)

    data object FinishWorkout : Button

    data class UpdateWorkoutName(val name: TextFieldState<String>) : Action

    data class UpdateWorkoutStartDateTime(
        val date: TextFieldState<LocalDate>,
        val time: TextFieldState<LocalTime>,
    ) : Action

    data class UpdateWorkoutEndDateTime(
        val date: TextFieldState<LocalDate>,
        val time: TextFieldState<LocalTime>,
    ) : Action

    data class UpdateWorkoutNotes(val notes: TextFieldState<String>) : Action
}

@Composable
fun Action.Button.getText(): String =
    when (this) {
        is Action.NextPage -> R.string.workout_action_next_exercise
        is Action.PreviousPage -> R.string.workout_action_previous_exercise
        is Action.FinishWorkout -> R.string.workout_summary_action_finish_workout
    }.let { stringResource(it) }

@Composable
fun Action.Button.getPainter() =
    when (this) {
        is Action.NextPage -> R.drawable.ic_arrow_forward
        is Action.PreviousPage -> R.drawable.ic_arrow_back
        is Action.FinishWorkout -> R.drawable.ic_check
    }.let { painterResource(it) }
