package com.patrykandpatrick.liftapp.feature.workout.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.text.TextFieldState
import com.patrykandpatrick.liftapp.ui.icons.ArrowBack
import com.patrykandpatrick.liftapp.ui.icons.ArrowForward
import com.patrykandpatrick.liftapp.ui.icons.Check
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import java.time.LocalDate
import java.time.LocalTime

sealed interface Action {
    sealed interface Button : Action

    sealed class MovePageBy(val delta: Int) : Button

    data class NextPage(val isLastExercise: Boolean) : MovePageBy(1)

    data object PreviousPage : MovePageBy(-1)

    data class SelectPage(val pageIndex: Int) : Action

    data class SaveSet(val workout: EditableWorkout, val item: WorkoutIterator.Item) : Action

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

    data class UpdateExerciseNotes(val exercise: EditableWorkout.Exercise, val notes: String) :
        Action

    data class AddSet(val exercises: List<EditableWorkout.Exercise>) : Action

    data class RemoveSet(val exercises: List<EditableWorkout.Exercise>) : Action

    data class PickExercises(val disabledExerciseIDs: List<Long>) : Action

    data class RemoveItem(val workoutItemID: Long) : Action

    data class ReorderItems(
        val workoutItemIDs: List<Long>,
        val selectedWorkoutItemID: Long?,
    ) : Action

    data class GoToExerciseDetails(val exerciseID: Long) : Action

    data object PopBackStack : Action
}

@Composable
fun Action.Button.getText(): String =
    stringResource(
        when (this) {
            is Action.NextPage -> {
                if (isLastExercise) {
                    R.string.workout_action_summary
                } else {
                    R.string.workout_action_next_exercise
                }
            }
            is Action.PreviousPage -> R.string.workout_action_previous_exercise
            is Action.FinishWorkout -> R.string.workout_summary_action_finish_workout
        }
    )

fun Action.Button.getImageVector() =
    when (this) {
        is Action.NextPage -> LiftAppIcons.ArrowForward
        is Action.PreviousPage -> LiftAppIcons.ArrowBack
        is Action.FinishWorkout -> LiftAppIcons.Check
    }
