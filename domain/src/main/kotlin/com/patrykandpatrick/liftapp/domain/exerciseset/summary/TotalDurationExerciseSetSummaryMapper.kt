package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject

class TotalDurationExerciseSetSummaryMapper @Inject constructor() :
    SetSplitExerciseSetSummaryMapper() {

    override suspend fun processSet(set: ExerciseSet, setIndex: Int): Double? =
        when (set) {
            is ExerciseSet.Cardio -> set.duration.inWholeMilliseconds.toDouble()

            is ExerciseSet.Time -> set.duration.inWholeMilliseconds.toDouble()

            is ExerciseSet.Calisthenics,
            is ExerciseSet.Reps,
            is ExerciseSet.Weight -> null
        }
}
