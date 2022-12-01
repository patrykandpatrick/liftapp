package com.patrykandpatryk.liftapp.feature.newroutine.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.toInvalid
import com.patrykandpatryk.liftapp.feature.newroutine.ui.ExerciseItem
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

sealed class ScreenState : Parcelable {

    abstract val name: Validatable<String>

    abstract val showErrors: Boolean

    abstract val exercises: List<ExerciseItem>

    val exerciseIds: List<Long>
        get() = exercises.map { it.id }

    abstract fun mutate(
        name: Validatable<String> = this.name,
        showErrors: Boolean = this.showErrors,
        exercises: List<ExerciseItem> = this.exercises,
    ): ScreenState

    @Parcelize
    @Immutable
    data class Insert(
        override val name: Validatable<String>,
        override val showErrors: Boolean = false,
        override val exercises: List<ExerciseItem> = emptyList(),
    ) : ScreenState() {

        override fun mutate(
            name: Validatable<String>,
            showErrors: Boolean,
            exercises: List<ExerciseItem>,
        ): ScreenState = copy(
            name = name,
            showErrors = showErrors,
            exercises = exercises,
        )
    }

    @Parcelize
    @Immutable
    data class Update(
        val id: Long,
        override val name: Validatable<String>,
        override val showErrors: Boolean = false,
        override val exercises: List<ExerciseItem> = emptyList(),
    ) : ScreenState() {

        override fun mutate(
            name: Validatable<String>,
            showErrors: Boolean,
            exercises: List<ExerciseItem>,
        ): ScreenState = copy(
            name = name,
            showErrors = showErrors,
            exercises = exercises,
        )
    }

    @Parcelize
    @Immutable
    object Loading : ScreenState() {

        @IgnoredOnParcel
        override val name: Validatable<String> = "".toInvalid()

        @IgnoredOnParcel
        override val showErrors: Boolean = false

        @IgnoredOnParcel
        override val exercises: List<ExerciseItem> = emptyList()

        override fun mutate(
            name: Validatable<String>,
            showErrors: Boolean,
            exercises: List<ExerciseItem>,
        ): ScreenState {
            error("Cannot mutate the `Loading` state.")
        }
    }
}
