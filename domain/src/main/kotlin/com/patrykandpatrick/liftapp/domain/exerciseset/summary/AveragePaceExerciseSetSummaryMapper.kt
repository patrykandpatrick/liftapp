package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject

class AveragePaceExerciseSetSummaryMapper
@Inject
constructor(private val unitConverter: UnitConverter) : SetSplitExerciseSetSummaryMapper() {

    override suspend fun processSet(set: ExerciseSet, setIndex: Int): Double? =
        when (set) {
            is ExerciseSet.Cardio -> {
                val distance = unitConverter.convertToPreferredUnit(set.distanceUnit, set.distance)
                set.duration.inWholeMilliseconds / distance
            }

            is ExerciseSet.Calisthenics,
            is ExerciseSet.Reps,
            is ExerciseSet.Time,
            is ExerciseSet.Weight -> null
        }
}
