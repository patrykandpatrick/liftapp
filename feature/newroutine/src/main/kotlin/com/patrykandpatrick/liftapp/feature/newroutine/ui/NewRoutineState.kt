package com.patrykandpatrick.liftapp.feature.newroutine.ui

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.core.text.StringTextFieldState
import com.patrykandpatrick.liftapp.core.ui.ErrorEffectState
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.validation.Validatable

@Stable
data class NewRoutineState(
    val id: Long,
    val routineName: String,
    val name: StringTextFieldState,
    val exercises: Validatable<List<RoutineExerciseItem>>,
    val isEdit: Boolean,
    val errorEffectState: ErrorEffectState,
    val showErrors: Boolean,
) {
    val exerciseIds: List<Long> = exercises.value.map { it.id }
}
