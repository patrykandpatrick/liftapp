package com.patrykandpatryk.liftapp.domain.exerciseset.summary

import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject

class TotalDurationExerciseSetSummaryMapper @Inject constructor() :
    SetSplitExerciseSetSummaryMapper() {

    override suspend fun processSet(set: ExerciseSet, setIndex: Int): Double? =
        when (set) {
            is ExerciseSet.Cardio -> set.duration.inWholeMilliseconds.toDouble()

            is ExerciseSet.Time -> set.duration.inWholeMilliseconds.toDouble()

            else -> null
        }
}
