package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.unit.EnergyUnit
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject

class TotalCaloriesExerciseSetSummaryMapper
@Inject
constructor(private val unitConverter: UnitConverter) : SetSplitExerciseSetSummaryMapper() {

    override suspend fun processSet(set: ExerciseSet, setIndex: Int): Double? =
        when (set) {
            is ExerciseSet.Cardio ->
                unitConverter.convertToPreferredUnit(EnergyUnit.KiloCalorie, set.kcal)
            is ExerciseSet.Calisthenics,
            is ExerciseSet.Reps,
            is ExerciseSet.Time,
            is ExerciseSet.Weight -> null
        }
}
