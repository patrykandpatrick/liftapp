package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatrick.liftapp.domain.exerciseset.OneRepMaxCalculator
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject

class OneRepMaxExerciseSetSummaryMapper
@Inject
constructor(private val unitConverter: UnitConverter) : ExerciseSetSummaryMapper {
    override suspend fun invoke(
        input: List<ExerciseSetGroup>
    ): List<Pair<List<Double>, List<Double>>> {
        val x = mutableListOf<Double>()
        val y = mutableListOf<Double>()

        input.forEachIndexed { _, group ->
            x += group.workoutStartDate.toLocalDate().toEpochDay().toDouble()
            val oneRm =
                group.sets.maxOf { set ->
                    when (set) {
                        is ExerciseSet.Calisthenics ->
                            OneRepMaxCalculator.getOneRepMax(
                                mass =
                                    unitConverter.convertToPreferredUnit(
                                        set.weightUnit,
                                        set.weight,
                                    ),
                                reps = set.reps,
                            )

                        is ExerciseSet.Weight ->
                            OneRepMaxCalculator.getOneRepMax(
                                mass =
                                    unitConverter.convertToPreferredUnit(
                                        set.weightUnit,
                                        set.weight,
                                    ),
                                reps = set.reps,
                            )

                        else -> 0.0
                    }
                }
            y += listOf(oneRm)
        }

        return listOf(x to y)
    }
}
