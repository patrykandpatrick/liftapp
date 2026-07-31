package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.ui.LiftAppModalBottomSheetWithTopAppBar
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppTextField

@Composable
internal fun ExerciseNotesBottomSheet(
    exercise: EditableWorkout.Exercise,
    onDismissRequest: () -> Unit,
    onSave: (String) -> Unit,
) {
    var notes by
        rememberSaveable(exercise.workoutItemID, exercise.id) { mutableStateOf(exercise.notes) }
    val focusManager = LocalFocusManager.current

    LiftAppModalBottomSheetWithTopAppBar(onDismissRequest = onDismissRequest) { dismiss ->
        WorkoutInputBottomSheetContent(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            LiftAppTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text(stringResource(R.string.generic_notes)) },
                singleLine = false,
                minLines = 3,
                maxLines = 8,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth(),
            )
            LiftAppButton(
                onClick = {
                    onSave(notes)
                    dismiss()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}
