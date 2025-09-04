package com.patrykandpatryk.liftapp.domain.exerciseset.summary

import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
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

            else -> null
        }
}
