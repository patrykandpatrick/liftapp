package com.patrykandpatryk.liftapp.core.exerciseset

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType

@Composable
fun ExerciseSummaryType.name(): String =
    when (this) {
        ExerciseSummaryType.OneRepMax -> stringResource(R.string.exercise_summary_type_1rm)
        ExerciseSummaryType.TotalVolume ->
            stringResource(R.string.exercise_summary_type_total_volume)
        ExerciseSummaryType.TotalReps -> stringResource(R.string.exercise_summary_type_total_reps)
        ExerciseSummaryType.TotalDistance ->
            stringResource(R.string.exercise_summary_type_total_distance)
        ExerciseSummaryType.TotalDuration ->
            stringResource(R.string.exercise_summary_type_total_duration)
        ExerciseSummaryType.AvgPace -> stringResource(R.string.exercise_summary_type_avg_pace)
        ExerciseSummaryType.AvgSpeed -> stringResource(R.string.exercise_summary_type_avg_speed)
        ExerciseSummaryType.TotalCalories ->
            stringResource(R.string.exercise_summary_type_total_calories)
    }
