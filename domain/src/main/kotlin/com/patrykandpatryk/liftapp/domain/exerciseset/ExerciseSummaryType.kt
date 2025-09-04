package com.patrykandpatryk.liftapp.domain.exerciseset

import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType.AvgPace
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType.AvgSpeed
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType.OneRepMax
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType.TotalCalories
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType.TotalDistance
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType.TotalDuration
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType.TotalReps
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType.TotalVolume

enum class ExerciseSummaryType {
    OneRepMax,
    TotalVolume,
    TotalReps,
    TotalDistance,
    TotalDuration,
    AvgPace,
    AvgSpeed,
    TotalCalories,
}

fun ExerciseType.getSummaryTypes(): List<ExerciseSummaryType> =
    when (this) {
        ExerciseType.Weight,
        ExerciseType.Calisthenics -> listOf(OneRepMax, TotalVolume, TotalReps)
        ExerciseType.Reps -> listOf(TotalReps)
        ExerciseType.Cardio ->
            listOf(AvgPace, TotalDistance, TotalDuration, AvgSpeed, TotalCalories)
        ExerciseType.Time -> listOf(TotalDuration)
    }
