package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject

class TotalVolumeExerciseSetSummaryMapper
@Inject
constructor(private val unitConverter: UnitConverter) : SetSplitExerciseSetSummaryMapper() {

    override suspend fun processSet(set: ExerciseSet, setIndex: Int): Double? =
        when (set) {
            is ExerciseSet.Calisthenics ->
                unitConverter.convertToPreferredUnit(set.weightUnit, set.weight) * set.reps

            is ExerciseSet.Weight ->
                unitConverter.convertToPreferredUnit(set.weightUnit, set.weight) * set.reps

            is ExerciseSet.Cardio,
            is ExerciseSet.Reps,
            is ExerciseSet.Time -> null
        }
}
