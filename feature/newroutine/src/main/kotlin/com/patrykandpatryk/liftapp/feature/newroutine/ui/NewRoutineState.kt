package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.validation.Validatable

@Stable
interface NewRoutineState {
    val name: TextFieldState<String>
    val exercises: State<Validatable<List<RoutineExerciseItem>>>
    val exerciseIds: List<Long>
    val isEdit: Boolean
    val showErrors: State<Boolean>
    val routineSaved: State<Boolean>
    val routineNotFound: State<Boolean>

    fun addPickedExercises(exerciseIds: List<Long>)
    fun removePickedExercise(exerciseId: Long)
    fun save()
}
