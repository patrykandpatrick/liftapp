package com.patrykandpatryk.liftapp.core.model

import android.icu.text.DecimalFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.patrykandpatryk.liftapp.core.R.plurals
import com.patrykandpatryk.liftapp.core.R.string
import com.patrykandpatryk.liftapp.core.extension.prettyString
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.text.MarkupProcessor
import com.patrykandpatryk.liftapp.core.time.getShortFormattedTime
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import kotlin.time.Duration

private val decimalFormat = DecimalFormat("#.##")

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
    if (distance > 0) {
        append(
            MarkupProcessor.Type.BoldSurfaceColor.wrap(
                "${decimalFormat.format(distance)} ${distanceUnit.prettyString()}"
            )
        )
    }
    if (duration.inWholeMilliseconds > 0) {
        if (isNotBlank()) append(" ${stringResource(string.goal_format_time_separator)} ")
        append(MarkupProcessor.Type.BoldSurfaceColor.wrap(duration.getShortFormattedTime()))
    }
    if (calories > 0) {
        if (isNotBlank()) append(" ${stringResource(string.goal_format_calories_separator)} ")
        append(
            MarkupProcessor.Type.BoldSurfaceColor.wrap(
                "${decimalFormat.format(calories)} ${stringResource(string.energy_unit_kcal)}"
            )
        )
    }
}

@Composable
fun getTimePrettyString(duration: Duration): String =
    MarkupProcessor.Type.BoldSurfaceColor.wrap(duration.getShortFormattedTime())
