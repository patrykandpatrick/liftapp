package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject

class TotalDistanceExerciseSetSummaryMapper
@Inject
constructor(private val unitConverter: UnitConverter) : SetSplitExerciseSetSummaryMapper() {

    override suspend fun processSet(set: ExerciseSet, setIndex: Int): Double? =
        when (set) {
            is ExerciseSet.Cardio ->
                unitConverter.convertToPreferredUnit(set.distanceUnit, set.distance)
            else -> null
        }
}
