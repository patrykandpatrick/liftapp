package com.patrykandpatrick.liftapp.feature.workout.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.prettyString
import com.patrykandpatryk.liftapp.core.text.DoubleTextFieldState
import com.patrykandpatryk.liftapp.core.text.IntTextFieldState
import com.patrykandpatryk.liftapp.core.text.LongTextFieldState
import com.patrykandpatryk.liftapp.core.time.getShortFormattedTime
import com.patrykandpatryk.liftapp.domain.Constants.Format.DECIMAL_PATTERN
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import java.io.Serializable
import java.text.DecimalFormat
import kotlin.time.Duration

sealed interface EditableExerciseSet<T : ExerciseSet> : Serializable {
    val isCompleted: Boolean

    val isInputValid: Boolean

    val suggestions: List<SetSuggestion<T>>

    fun applySet(set: T)

    data class Weight(
        override val weight: Double,
        override val reps: Int,
        val weightInput: DoubleTextFieldState,
        val repsInput: IntTextFieldState,
        override val weightUnit: MassUnit,
        override val suggestions: List<SetSuggestion<ExerciseSet.Weight>>,
    ) : ExerciseSet.Weight(weight, reps, weightUnit), EditableExerciseSet<ExerciseSet.Weight> {

        override val isInputValid: Boolean
            get() = weightInput.isValid && repsInput.isValid

        override fun applySet(set: ExerciseSet.Weight) {
            weightInput.updateValue(set.weight)
            repsInput.updateValue(set.reps)
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
        override val suggestions: List<SetSuggestion<ExerciseSet.Calisthenics>>,
    ) :
        ExerciseSet.Calisthenics(weight, bodyWeight, reps, weightUnit),
        EditableExerciseSet<ExerciseSet.Calisthenics> {

        override val isInputValid: Boolean
            get() = weightInput.isValid && repsInput.isValid

        override fun applySet(set: ExerciseSet.Calisthenics) {
            weightInput.updateValue(set.weight)
            repsInput.updateValue(set.reps)
        }
    }

    data class Reps(
        override val reps: Int,
        val repsInput: IntTextFieldState,
        override val suggestions: List<SetSuggestion<ExerciseSet.Reps>>,
    ) : ExerciseSet.Reps(reps), EditableExerciseSet<ExerciseSet.Reps> {

        override val isInputValid: Boolean
            get() = repsInput.isValid

        override fun applySet(set: ExerciseSet.Reps) {
            repsInput.updateValue(set.reps)
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
        override val suggestions: List<SetSuggestion<ExerciseSet.Cardio>>,
    ) :
        ExerciseSet.Cardio(duration, distance, kcal, distanceUnit),
        EditableExerciseSet<ExerciseSet.Cardio> {

        override val isInputValid: Boolean
            get() = durationInput.isValid && distanceInput.isValid && kcalInput.isValid

        override fun applySet(set: ExerciseSet.Cardio) {
            durationInput.updateValue(set.duration.inWholeMilliseconds)
            distanceInput.updateValue(set.distance)
            kcalInput.updateValue(set.kcal)
        }
    }

    data class Time(
        override val duration: Duration,
        val timeInput: LongTextFieldState,
        override val suggestions: List<SetSuggestion<ExerciseSet.Time>>,
    ) : ExerciseSet.Time(duration), EditableExerciseSet<ExerciseSet.Time> {

        override val isInputValid: Boolean
            get() = timeInput.isValid

        override fun applySet(set: ExerciseSet.Time) {
            timeInput.updateValue(set.duration.inWholeMilliseconds)
        }
    }

    data class SetSuggestion<T : ExerciseSet>(val set: T, val type: Type) {
        enum class Type {
            PreviousSet,
            PreviousWorkout,
        }
    }
}

@Composable
fun EditableExerciseSet<ExerciseSet>.prettyString(): String = (this as ExerciseSet).prettyString()

@Composable
fun ExerciseSet.prettyString(): String =
    if (isCompleted) {
        val decimalFormat = remember { DecimalFormat(DECIMAL_PATTERN) }
        when (this) {
            is ExerciseSet.Weight ->
                stringResource(
                    R.string.exercise_set_format_weight,
                    decimalFormat.format(weight),
                    weightUnit.prettyString(),
                    reps,
                    getRepsString(reps),
                )

            is ExerciseSet.Calisthenics ->
                if (weight > 0) {
                    stringResource(
                        R.string.exercise_set_format_calisthenics_with_weight,
                        decimalFormat.format(bodyWeight),
                        decimalFormat.format(weight),
                        weightUnit.prettyString(),
                        reps,
                        getRepsString(reps),
                    )
                } else {
                    stringResource(
                        R.string.exercise_set_format_calisthenics,
                        decimalFormat.format(weight),
                        weightUnit.prettyString(),
                        reps,
                        getRepsString(reps),
                    )
                }

            is ExerciseSet.Reps ->
                stringResource(R.string.exercise_set_format_reps, reps, getRepsString(reps))

            is ExerciseSet.Cardio -> {
                stringResource(
                    R.string.exercise_set_format_cardio,
                    duration.getShortFormattedTime(),
                    "${decimalFormat.format(distance)} ${distanceUnit.prettyString()}",
                    "${decimalFormat.format(kcal)} ${stringResource(R.string.energy_unit_kcal)}",
                )
            }
            is ExerciseSet.Time -> duration.getShortFormattedTime()
        }
    } else {
        stringResource(R.string.exercise_set_not_completed)
    }

@Composable
private fun getRepsString(reps: Int): String = pluralStringResource(R.plurals.rep_count, reps)
