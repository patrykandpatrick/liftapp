package com.patrykandpatryk.liftapp.feature.newexercise.state

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.validation.Validable
import com.patrykandpatryk.liftapp.domain.validation.toInValid
import java.io.Serializable

sealed class NewExerciseState {

    abstract val name: Validable<String>
    abstract val type: ExerciseType
    abstract val mainMuscles: Validable<List<Muscle>>
    abstract val secondaryMuscles: List<Muscle>
    abstract val tertiaryMuscles: List<Muscle>

    abstract val showErrors: Boolean

    val disabledMainMuscles by lazy { secondaryMuscles + tertiaryMuscles }

    val disabledSecondaryMuscles by lazy { mainMuscles.value + tertiaryMuscles }

    val disabledTertiaryMuscles by lazy { mainMuscles.value + secondaryMuscles }

    val showNameError: Boolean
        get() = showErrors && name.isInvalid

    val showMainMusclesError: Boolean
        get() = showErrors && mainMuscles.isInvalid

    fun copyState(
        name: Validable<String> = this.name,
        type: ExerciseType = this.type,
        mainMuscles: Validable<List<Muscle>> = this.mainMuscles,
        secondaryMuscles: List<Muscle> = this.secondaryMuscles,
        tertiaryMuscles: List<Muscle> = this.tertiaryMuscles,
    ): NewExerciseState = when {
        name is Validable.Valid && mainMuscles is Validable.Valid -> Valid(
            name = name,
            type = type,
            mainMuscles = mainMuscles,
            secondaryMuscles = secondaryMuscles,
            tertiaryMuscles = tertiaryMuscles,
        )
        else -> Invalid(
            name = name,
            type = type,
            mainMuscles = mainMuscles,
            secondaryMuscles = secondaryMuscles,
            tertiaryMuscles = tertiaryMuscles,
            showErrors = showErrors && (name.isValid.not() || mainMuscles.isValid.not()),
        )
    }

    @Immutable
    data class Valid(
        override val name: Validable.Valid<String>,
        override val type: ExerciseType,
        override val mainMuscles: Validable.Valid<List<Muscle>>,
        override val secondaryMuscles: List<Muscle>,
        override val tertiaryMuscles: List<Muscle>,
    ) : NewExerciseState(), Serializable {

        override val showErrors: Boolean = false

        companion object {
            const val serialVersionUID = 1L
        }
    }

    @Immutable
    data class Invalid(
        override val name: Validable<String> = "".toInValid(),
        override val type: ExerciseType = ExerciseType.Cardio,
        override val mainMuscles: Validable<List<Muscle>> = emptyList<Muscle>().toInValid(),
        override val secondaryMuscles: List<Muscle> = emptyList(),
        override val tertiaryMuscles: List<Muscle> = emptyList(),
        override val showErrors: Boolean = false,
    ) : NewExerciseState(), Serializable {

        companion object {
            const val serialVersionUID = 1L
        }
    }
}
