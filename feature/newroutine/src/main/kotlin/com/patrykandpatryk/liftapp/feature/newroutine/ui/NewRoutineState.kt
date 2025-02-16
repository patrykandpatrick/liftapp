package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.core.text.StringTextFieldState
import com.patrykandpatryk.liftapp.core.ui.ErrorEffectState
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.validation.Validatable

@Stable
data class NewRoutineState(
    val id: Long,
    val name: StringTextFieldState,
    val exercises: Validatable<List<RoutineExerciseItem>>,
    val isEdit: Boolean,
    val errorEffectState: ErrorEffectState,
    val showErrors: Boolean,
    val routineSaved: Boolean,
) {
    val exerciseIds: List<Long> = exercises.value.map { it.id }
}
