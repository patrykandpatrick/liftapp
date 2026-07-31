package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.model.getDisplayName
import com.patrykandpatrick.liftapp.core.ui.LiftAppModalBottomSheetWithTopAppBar
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
internal fun SetInputBottomSheet(
    exercise: EditableWorkout.Exercise,
    set: EditableExerciseSet<*>,
    setIndex: Int,
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
) {
    var isSaving by remember(exercise.id, setIndex) { mutableStateOf(false) }
    val initialInputTexts = remember(set) { set.inputTexts() }

    fun dismiss() {
        if (!isSaving) set.restoreInputTexts(initialInputTexts)
        onDismissRequest()
    }

    LiftAppModalBottomSheetWithTopAppBar(
        onDismissRequest = ::dismiss,
        title = {
            Text(
                text =
                    if (exercise.isSuperset) {
                        exercise.name.getDisplayName()
                    } else {
                        stringResource(R.string.workout_set_input_title, setIndex + 1)
                    },
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )
        },
    ) { dismissSheet ->
        WorkoutInputBottomSheetContent(modifier = Modifier.verticalScroll(rememberScrollState())) {
            SetEditorContent(set)
            LiftAppButton(
                onClick = {
                    isSaving = true
                    onSave()
                    dismissSheet()
                },
                enabled = set.isInputValid,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

private fun EditableExerciseSet<*>.inputTexts(): List<String> =
    when (this) {
        is EditableExerciseSet.Weight -> listOf(weightInput.text, repsInput.text, notesInput.text)
        is EditableExerciseSet.Calisthenics ->
            listOf(weightInput.text, repsInput.text, notesInput.text)
        is EditableExerciseSet.Reps -> listOf(repsInput.text, notesInput.text)
        is EditableExerciseSet.Cardio ->
            listOf(durationInput.text, distanceInput.text, kcalInput.text, notesInput.text)
        is EditableExerciseSet.Time -> listOf(timeInput.text, notesInput.text)
    }

private fun EditableExerciseSet<*>.restoreInputTexts(texts: List<String>) {
    when (this) {
        is EditableExerciseSet.Weight -> {
            weightInput.updateText(texts[0])
            repsInput.updateText(texts[1])
            notesInput.updateText(texts[2])
        }
        is EditableExerciseSet.Calisthenics -> {
            weightInput.updateText(texts[0])
            repsInput.updateText(texts[1])
            notesInput.updateText(texts[2])
        }
        is EditableExerciseSet.Reps -> {
            repsInput.updateText(texts[0])
            notesInput.updateText(texts[1])
        }
        is EditableExerciseSet.Cardio -> {
            durationInput.updateText(texts[0])
            distanceInput.updateText(texts[1])
            kcalInput.updateText(texts[2])
            notesInput.updateText(texts[3])
        }
        is EditableExerciseSet.Time -> {
            timeInput.updateText(texts[0])
            notesInput.updateText(texts[1])
        }
    }
}
