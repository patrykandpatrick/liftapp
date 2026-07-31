package com.patrykandpatrick.liftapp.feature.workout.model

import androidx.compose.runtime.Composable
import com.patrykandpatrick.liftapp.core.exercise.prettyString
import com.patrykandpatrick.liftapp.core.text.DoubleTextFieldState
import com.patrykandpatrick.liftapp.core.text.IntTextFieldState
import com.patrykandpatrick.liftapp.core.text.LongTextFieldState
import com.patrykandpatrick.liftapp.core.text.TextFieldState
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import java.io.Serializable
import kotlin.time.Duration

sealed interface EditableExerciseSet<out T : ExerciseSet> : Serializable {
    val isCompleted: Boolean

    val isInputValid: Boolean

    val notesInput: TextFieldState<String>

    val exerciseSet: T

    fun applySet(set: @UnsafeVariance T)

    data class Weight(
        override val weight: Double,
        override val reps: Int,
        val weightInput: DoubleTextFieldState,
        val repsInput: IntTextFieldState,
        override val weightUnit: MassUnit,
        override val notesInput: TextFieldState<String>,
    ) :
        ExerciseSet.Weight(weight, reps, weightUnit, notesInput.value),
        EditableExerciseSet<ExerciseSet.Weight> {

        override val exerciseSet: ExerciseSet.Weight = this

        override val isInputValid: Boolean
            get() = weightInput.isValid && repsInput.isValid

        override fun applySet(set: ExerciseSet.Weight) {
            weightInput.updateValue(set.weight)
            repsInput.updateValue(set.reps)
            notesInput.updateValue(set.notes)
        }
    }

    data class Calisthenics(
        override val weight: Double,
        override val bodyWeight: Double,
        override val reps: Int,
        val formattedBodyWeight: String,
        val weightInput: DoubleTextFieldState,
        val repsInput: IntTextFieldState,
        override val weightUnit: MassUnit,
        override val notesInput: TextFieldState<String>,
    ) :
        ExerciseSet.Calisthenics(weight, bodyWeight, reps, weightUnit, notesInput.value),
        EditableExerciseSet<ExerciseSet.Calisthenics> {

        override val exerciseSet: ExerciseSet.Calisthenics = this

        override val isInputValid: Boolean
            get() = weightInput.isValid && repsInput.isValid

        override fun applySet(set: ExerciseSet.Calisthenics) {
            weightInput.updateValue(set.weight)
            repsInput.updateValue(set.reps)
            notesInput.updateValue(set.notes)
        }
    }

    data class Reps(
        override val reps: Int,
        val repsInput: IntTextFieldState,
        override val notesInput: TextFieldState<String>,
    ) : ExerciseSet.Reps(reps, notesInput.value), EditableExerciseSet<ExerciseSet.Reps> {

        override val exerciseSet: ExerciseSet.Reps = this

        override val isInputValid: Boolean
            get() = repsInput.isValid

        override fun applySet(set: ExerciseSet.Reps) {
            repsInput.updateValue(set.reps)
            notesInput.updateValue(set.notes)
        }
    }

    data class Cardio(
        override val duration: Duration,
        override val distance: Double,
        override val kcal: Double,
        val durationInput: LongTextFieldState,
        val distanceInput: DoubleTextFieldState,
        val kcalInput: DoubleTextFieldState,
        override val distanceUnit: LongDistanceUnit,
        override val notesInput: TextFieldState<String>,
    ) :
        ExerciseSet.Cardio(duration, distance, kcal, distanceUnit, notesInput.value),
        EditableExerciseSet<ExerciseSet.Cardio> {

        override val exerciseSet: ExerciseSet.Cardio = this

        override val isInputValid: Boolean
            get() = durationInput.isValid && distanceInput.isValid && kcalInput.isValid

        override fun applySet(set: ExerciseSet.Cardio) {
            durationInput.updateValue(set.duration.inWholeMilliseconds)
            distanceInput.updateValue(set.distance)
            kcalInput.updateValue(set.kcal)
            notesInput.updateValue(set.notes)
        }
    }

    data class Time(
        override val duration: Duration,
        val timeInput: LongTextFieldState,
        override val notesInput: TextFieldState<String>,
    ) : ExerciseSet.Time(duration, notesInput.value), EditableExerciseSet<ExerciseSet.Time> {

        override val exerciseSet: ExerciseSet.Time = this

        override val isInputValid: Boolean
            get() = timeInput.isValid

        override fun applySet(set: ExerciseSet.Time) {
            timeInput.updateValue(set.duration.inWholeMilliseconds)
            notesInput.updateValue(set.notes)
        }
    }
}

@Composable
fun EditableExerciseSet<ExerciseSet>.prettyString(): String = (this as ExerciseSet).prettyString()
