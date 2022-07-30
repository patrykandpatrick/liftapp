package com.patrykandpatryk.liftapp.feature.newexercise.state

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.toInvalid
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

sealed class NewExerciseState {

    abstract val id: Long
    abstract val name: Validatable<Name>
    abstract val displayName: String
    abstract val type: ExerciseType
    abstract val mainMuscles: Validatable<List<Muscle>>
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
        name: Validatable<Name> = this.name,
        displayName: String = this.displayName,
        type: ExerciseType = this.type,
        mainMuscles: Validatable<List<Muscle>> = this.mainMuscles,
        secondaryMuscles: List<Muscle> = this.secondaryMuscles,
        tertiaryMuscles: List<Muscle> = this.tertiaryMuscles,
    ): NewExerciseState = when {
        name is Validatable.Valid && mainMuscles is Validatable.Valid -> Valid(
            name = name,
            displayName = displayName,
            type = type,
            mainMuscles = mainMuscles,
            secondaryMuscles = secondaryMuscles,
            tertiaryMuscles = tertiaryMuscles,
            id = id,
        )
        else -> Invalid(
            name = name,
            displayName = displayName,
            type = type,
            mainMuscles = mainMuscles,
            secondaryMuscles = secondaryMuscles,
            tertiaryMuscles = tertiaryMuscles,
            showErrors = showErrors && (name.isValid.not() || mainMuscles.isValid.not()),
            id = id,
        )
    }

    @Parcelize
    @Immutable
    data class Valid(
        override val name: Validatable.Valid<Name>,
        override val displayName: String,
        override val type: ExerciseType,
        override val mainMuscles: Validatable.Valid<List<Muscle>>,
        override val secondaryMuscles: List<Muscle>,
        override val tertiaryMuscles: List<Muscle>,
        override val id: Long = ID_NOT_SET,
    ) : NewExerciseState(), Parcelable {

        @IgnoredOnParcel
        override val showErrors: Boolean = false

        companion object {
            const val serialVersionUID = 1L
        }
    }

    @Parcelize
    @Immutable
    data class Invalid(
        override val name: Validatable<Name> = Name.Empty.toInvalid(),
        override val displayName: String = Name.Empty.value,
        override val type: ExerciseType = ExerciseType.Cardio,
        override val mainMuscles: Validatable<List<Muscle>> = emptyList<Muscle>().toInvalid(),
        override val secondaryMuscles: List<Muscle> = emptyList(),
        override val tertiaryMuscles: List<Muscle> = emptyList(),
        override val showErrors: Boolean = false,
        override val id: Long = ID_NOT_SET,
    ) : NewExerciseState(), Parcelable {

        companion object {
            const val serialVersionUID = 1L
        }
    }
}
