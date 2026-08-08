package com.patrykandpatrick.liftapp.core.exercise

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.format.LocalFormatter
import com.patrykandpatrick.liftapp.domain.unit.EnergyUnit
import com.patrykandpatrick.liftapp.domain.unit.RepsUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet

@Composable
fun ExerciseSet?.prettyString(): String =
    if (this?.isCompleted == true) {
        withRir(setString())
    } else {
        stringResource(R.string.exercise_set_not_completed)
    }

/**
 * Renders a reps-in-reserve value. The top of [ExerciseSet.RIR_RANGE] is an open bucket — the user
 * picked "four or more" — so it has to read as one everywhere, not only in the selector that
 * collected it.
 */
@Composable
fun rirString(rir: Int): String =
    if (rir >= ExerciseSet.RIR_RANGE.last) {
        stringResource(R.string.exercise_set_rir_max, ExerciseSet.RIR_RANGE.last)
    } else {
        rir.toString()
    }

/** Appends the reported reps in reserve, when the user reported any. */
@Composable
private fun ExerciseSet.withRir(text: String): String =
    rir?.let { stringResource(R.string.exercise_set_format_rir, text, rirString(it)) } ?: text

@Composable
private fun ExerciseSet.setString(): String {
    val formatter = LocalFormatter.current
    return when (this) {
        is ExerciseSet.Weight ->
            stringResource(
                R.string.exercise_set_format_weight,
                formatter.formatValue(weight, weightUnit),
                formatter.formatValue(reps, RepsUnit),
            )

        is ExerciseSet.Calisthenics ->
            if (weight > 0) {
                stringResource(
                    R.string.exercise_set_format_calisthenics_with_weight,
                    formatter.formatValue(bodyWeight, weightUnit),
                    formatter.formatValue(weight, weightUnit),
                    formatter.formatValue(reps, RepsUnit),
                )
            } else {
                stringResource(
                    R.string.exercise_set_format_calisthenics,
                    formatter.formatValue(bodyWeight, weightUnit),
                    formatter.formatValue(reps, RepsUnit),
                )
            }

        is ExerciseSet.Reps -> formatter.formatValue(reps, RepsUnit)

        is ExerciseSet.Cardio -> {
            stringResource(
                R.string.exercise_set_format_cardio,
                formatter.formatDuration(duration),
                formatter.formatValue(distance, distanceUnit),
                formatter.formatValue(kcal, EnergyUnit.KiloCalorie),
            )
        }
        is ExerciseSet.Time -> formatter.formatDuration(duration)
    }
}
