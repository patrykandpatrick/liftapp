package com.patrykandpatryk.liftapp.core.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.patrykandpatryk.liftapp.core.R.plurals
import com.patrykandpatryk.liftapp.core.R.string
import com.patrykandpatryk.liftapp.core.format.LocalFormatter
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.markup.MarkupType
import com.patrykandpatryk.liftapp.domain.unit.EnergyUnit
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import kotlin.time.Duration

@Stable
@Composable
fun Goal.getPrettyStringLong(exerciseType: ExerciseType): AnnotatedString {
    val text =
        when (exerciseType) {
            ExerciseType.Weight,
            ExerciseType.Calisthenics,
            ExerciseType.Reps -> getRepsPrettyString(minReps, maxReps, sets)

            ExerciseType.Cardio -> getCardioPrettyString(distance, distanceUnit, duration, calories)
            ExerciseType.Time -> getTimePrettyString(duration)
        }
    return LocalMarkupProcessor.current.toAnnotatedString(text)
}

@Composable
fun getRepsPrettyString(minReps: Int, maxReps: Int, sets: Int): String =
    if (minReps == maxReps) {
        stringResource(
            string.goal_reps_format_long_single_rep,
            maxReps,
            pluralStringResource(plurals.rep_count, maxReps),
            sets,
            pluralStringResource(plurals.set_count, sets),
        )
    } else {
        stringResource(
            string.goal_reps_format_long,
            minReps,
            maxReps,
            pluralStringResource(plurals.rep_count, maxReps),
            sets,
            pluralStringResource(plurals.set_count, sets),
        )
    }

@Composable
fun getCardioPrettyString(
    distance: Double,
    distanceUnit: LongDistanceUnit,
    duration: Duration,
    calories: Double,
): String = buildString {
    val formatter = LocalFormatter.current

    if (distance > 0) {
        append(
            MarkupType.wrap(
                formatter.formatValue(distance, distanceUnit),
                MarkupType.Style.Bold,
                MarkupType.Color.SurfaceVariant,
            )
        )
    }
    if (duration.inWholeMilliseconds > 0) {
        if (isNotBlank()) append(" ${stringResource(string.goal_format_time_separator)} ")
        append(
            MarkupType.wrap(
                formatter.formatDuration(duration),
                MarkupType.Style.Bold,
                MarkupType.Color.SurfaceVariant,
            )
        )
    }
    if (calories > 0) {
        if (isNotBlank()) append(" ${stringResource(string.point_separator)} ")
        append(
            MarkupType.wrap(
                formatter.formatValue(calories, EnergyUnit.KiloCalorie),
                MarkupType.Style.Bold,
                MarkupType.Color.SurfaceVariant,
            )
        )
    }
}

@Composable
fun getTimePrettyString(duration: Duration): String =
    MarkupType.wrap(
        LocalFormatter.current.formatDuration(duration),
        MarkupType.Style.Bold,
        MarkupType.Color.SurfaceVariant,
    )
