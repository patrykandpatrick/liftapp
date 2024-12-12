package com.patrykandpatryk.liftapp.feature.newroutine.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class ScreenState(
    val id: Long,
    val name: Validatable<String>,
    val exercises: Validatable<List<RoutineExerciseItem>>,
    val isEdit: Boolean = false,
    val showErrors: Boolean = false,
) : Parcelable {

    val exerciseIds: List<Long>
        get() = exercises.value.map { it.id }
}
