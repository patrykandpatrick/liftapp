package com.patrykandpatrick.liftapp.feature.routine.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.text.IntTextFieldState
import com.patrykandpatrick.liftapp.core.text.LongTextFieldState
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItem

@Stable
data class SupersetEditorState(
    val includedExercises: List<RoutineExerciseItem>,
    val sets: IntTextFieldState,
    val restTime: LongTextFieldState,
    val error: Error?,
) {
    sealed class Error {
        data object TooFewExercises : Error()

        data object TooManyExercises : Error()
    }
}

@Composable
internal fun SupersetEditorState.Error.getText(): String =
    when (this) {
        SupersetEditorState.Error.TooFewExercises ->
            stringResource(R.string.superset_error_too_few_exercises)
        SupersetEditorState.Error.TooManyExercises ->
            stringResource(
                R.string.superset_error_too_many_exercises,
                RoutineItem.MAX_SUPERSET_SIZE,
            )
    }
