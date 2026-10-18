package com.patrykandpatrick.liftapp.core.ui.resource

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.exercise.rirString
import com.patrykandpatrick.liftapp.core.format.LocalFormatter
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.EnergyUnit
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import kotlin.math.roundToInt

/**
 * One set's prescription in a line, for showing beside the set being logged.
 *
 * Which fields carry is decided by [exerciseType] rather than by which are non-zero: a run has a
 * distance and no reps, and a rep target of zero on a run is an absence, not a prescription of
 * none.
 */
@Composable
fun SetTarget.prettyString(
    exerciseType: ExerciseType,
    distanceUnit: LongDistanceUnit = LongDistanceUnit.Kilometer,
): String =
    when (exerciseType) {
        ExerciseType.Weight -> withLoadPercent(repsWithRir())
        ExerciseType.Calisthenics,
        ExerciseType.Reps -> repsWithRir()
        ExerciseType.Cardio -> cardio(distanceUnit)
        ExerciseType.Time -> LocalFormatter.current.formatDuration(duration)
    }

@Composable
private fun SetTarget.repsWithRir(): String {
    val reps =
        if (minReps == maxReps) {
            stringResource(
                R.string.workout_set_target_reps_single,
                maxReps,
                pluralStringResource(R.plurals.rep_count, maxReps),
            )
        } else {
            stringResource(
                R.string.workout_set_target_reps_range,
                minReps,
                maxReps,
                pluralStringResource(R.plurals.rep_count, maxReps),
            )
        }
    return rir?.let { stringResource(R.string.exercise_set_format_rir, reps, rirString(it)) }
        ?: reps
}

@Composable
private fun SetTarget.withLoadPercent(reps: String): String =
    loadPercent?.let { percent ->
        stringResource(R.string.workout_set_target_load_percent, reps, percent.roundToInt())
    } ?: reps

@Composable
private fun SetTarget.cardio(distanceUnit: LongDistanceUnit): String = buildString {
    val formatter = LocalFormatter.current
    if (distance > 0) append(formatter.formatValue(distance, distanceUnit))
    if (duration.inWholeMilliseconds > 0) {
        if (isNotEmpty()) append(" ${stringResource(R.string.goal_format_time_separator)} ")
        append(formatter.formatDuration(duration))
    }
    if (calories > 0) {
        if (isNotEmpty()) append(" ${stringResource(R.string.point_separator)} ")
        append(formatter.formatValue(calories, EnergyUnit.KiloCalorie))
    }
}
