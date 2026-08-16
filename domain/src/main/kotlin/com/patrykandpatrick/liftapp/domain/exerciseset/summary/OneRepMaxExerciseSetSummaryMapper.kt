package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatrick.liftapp.domain.exerciseset.OneRepMaxCalculator
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
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
        // The one-rep max curve is unit-dependent, so it has to be told which unit these masses
        // were converted into.
        val preferredUnit = unitConverter.getPreferredUnit(MassUnit.Kilograms)

        input.forEachIndexed { _, group ->
            x += group.workoutStartDate.toLocalDate().toEpochDay().toDouble()
            val oneRm =
                group.sets.maxOf { set ->
                    when (set) {
                        is ExerciseSet.Calisthenics ->
                            oneRepMax(set.weight, set.reps, set.weightUnit, set.rir, preferredUnit)

                        is ExerciseSet.Weight ->
                            oneRepMax(set.weight, set.reps, set.weightUnit, set.rir, preferredUnit)

                        is ExerciseSet.Cardio,
                        is ExerciseSet.Reps,
                        is ExerciseSet.Time -> 0.0
                    }
                }
            y += listOf(oneRm)
        }

        return listOf(x to y)
    }

    /**
     * A set of 8 at 2 RIR is a 10-rep max, so a set stopped short reads higher here than the same
     * set taken to failure — which is the point of recording RIR at all.
     *
     * A null [rir] is not a zero: it is a set nobody reported on, and the only reading left is the
     * one this chart used before RIR existed, that the reps logged were the reps available. Sets
     * logged before then keep the value they always had.
     */
    private suspend fun oneRepMax(
        weight: Double,
        reps: Int,
        weightUnit: MassUnit,
        rir: Int?,
        preferredUnit: MassUnit,
    ): Double {
        val mass = unitConverter.convertToPreferredUnit(weightUnit, weight)
        return if (rir == null) {
            OneRepMaxCalculator.getOneRepMax(mass, reps, preferredUnit)
        } else {
            OneRepMaxCalculator.estimatedOneRepMax(mass, reps, rir, preferredUnit)
        }
    }
}
