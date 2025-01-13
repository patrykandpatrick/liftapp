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
import java.io.Serializable
import java.text.DecimalFormat
import kotlin.time.Duration

sealed class EditableExerciseSet : Serializable {
    abstract val isCompleted: Boolean

    abstract val isInputValid: Boolean

    data class Weight(
        val weight: Double,
        val reps: Int,
        val weightInput: DoubleTextFieldState,
        val repsInput: IntTextFieldState,
        val weightUnit: MassUnit,
    ) : EditableExerciseSet() {
        override val isCompleted: Boolean
            get() = weight > 0 && reps > 0

        override val isInputValid: Boolean
            get() = weightInput.isValid && repsInput.isValid
    }

    data class Calisthenics(
        val weight: Double,
        val bodyWeight: Double,
        val reps: Int,
        val weightInput: DoubleTextFieldState,
        val bodyWeightInput: DoubleTextFieldState,
        val repsInput: IntTextFieldState,
        val weightUnit: MassUnit,
    ) : EditableExerciseSet() {
        override val isCompleted: Boolean
            get() = weight > 0 && reps > 0

        override val isInputValid: Boolean
            get() = weightInput.isValid && repsInput.isValid && bodyWeightInput.isValid
    }

    data class Reps(val reps: Int, val repsInput: IntTextFieldState) : EditableExerciseSet() {
        override val isCompleted: Boolean
            get() = reps > 0

        override val isInputValid: Boolean
            get() = repsInput.isValid
    }

    data class Cardio(
        val duration: Duration,
        val distance: Double,
        val kcal: Double,
        val durationInput: LongTextFieldState,
        val distanceInput: DoubleTextFieldState,
        val kcalInput: DoubleTextFieldState,
        val distanceUnit: LongDistanceUnit,
    ) : EditableExerciseSet() {
        override val isCompleted: Boolean
            get() = duration.inWholeSeconds > 0 && distance > 0

        override val isInputValid: Boolean
            get() = durationInput.isValid && distanceInput.isValid && kcalInput.isValid
    }

    data class Time(val duration: Duration, val timeInput: LongTextFieldState) :
        EditableExerciseSet() {
        override val isCompleted: Boolean
            get() = duration.inWholeSeconds > 0

        override val isInputValid: Boolean
            get() = timeInput.isValid
    }
}

@Composable
fun EditableExerciseSet.prettyString(): String =
    if (isCompleted) {
        val decimalFormat = remember { DecimalFormat(DECIMAL_PATTERN) }
        when (this) {
            is EditableExerciseSet.Weight ->
                stringResource(
                    R.string.exercise_set_format_weight,
                    decimalFormat.format(weight),
                    weightUnit.prettyString(),
                    reps,
                    getRepsString(reps),
                )

            is EditableExerciseSet.Calisthenics ->
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

            is EditableExerciseSet.Reps ->
                stringResource(R.string.exercise_set_format_reps, reps, getRepsString(reps))

            is EditableExerciseSet.Cardio -> {
                stringResource(
                    R.string.exercise_set_format_cardio,
                    duration.getShortFormattedTime(),
                    "${decimalFormat.format(distance)} ${distanceUnit.prettyString()}",
                    "${decimalFormat.format(kcal)} ${stringResource(R.string.energy_unit_kcal)}",
                )
            }
            is EditableExerciseSet.Time -> duration.getShortFormattedTime()
        }
    } else {
        stringResource(R.string.exercise_set_not_completed)
    }

@Composable
private fun getRepsString(reps: Int): String = pluralStringResource(R.plurals.rep_count, reps)
