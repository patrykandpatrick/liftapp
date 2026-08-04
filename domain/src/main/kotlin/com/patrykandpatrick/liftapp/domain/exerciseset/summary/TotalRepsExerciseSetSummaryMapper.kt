package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject

class TotalRepsExerciseSetSummaryMapper @Inject constructor() : SetSplitExerciseSetSummaryMapper() {

    override suspend fun processSet(set: ExerciseSet, setIndex: Int): Double? =
        when (set) {
            is ExerciseSet.Calisthenics -> set.reps.toDouble()
            is ExerciseSet.Weight -> set.reps.toDouble()
            is ExerciseSet.Reps -> set.reps.toDouble()
            is ExerciseSet.Cardio,
            is ExerciseSet.Time -> null
        }
}
