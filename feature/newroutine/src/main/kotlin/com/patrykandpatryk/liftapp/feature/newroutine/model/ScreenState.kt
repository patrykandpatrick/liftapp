package com.patrykandpatryk.liftapp.feature.newroutine.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.feature.newroutine.ui.ExerciseItem
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class ScreenState(
    val id: Long,
    val name: Validatable<String>,
    val exercises: Validatable<List<ExerciseItem>>,
    val isEdit: Boolean = false,
    val showErrors: Boolean = false,
) : Parcelable {

    val exerciseIds: List<Long>
        get() = exercises.value.map { it.id }
}
