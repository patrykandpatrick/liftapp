package com.patrykandpatryk.liftapp.core.exercise

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.format.LocalFormatter
import com.patrykandpatryk.liftapp.domain.unit.EnergyUnit
import com.patrykandpatryk.liftapp.domain.unit.RepsUnit
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet

@Composable
fun ExerciseSet.prettyString(): String =
    if (isCompleted) {
        val formatter = LocalFormatter.current
        when (this) {
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
    } else {
        stringResource(R.string.exercise_set_not_completed)
    }
