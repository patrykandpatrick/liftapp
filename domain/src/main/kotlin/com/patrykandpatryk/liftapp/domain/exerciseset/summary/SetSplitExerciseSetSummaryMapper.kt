package com.patrykandpatryk.liftapp.domain.exerciseset.summary

import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatryk.liftapp.domain.extension.getOrPut
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet

abstract class SetSplitExerciseSetSummaryMapper : ExerciseSetSummaryMapper {
    final override suspend fun invoke(
        input: List<ExerciseSetGroup>
    ): List<Pair<List<Double>, List<Double>>> {
        val x = mutableListOf<Double>()
        val y = mutableListOf<MutableList<Double>>()

        input.forEachIndexed { index, group ->
            x += group.workoutStartDate.toLocalDate().toEpochDay().toDouble()
            group.sets.forEachIndexed { index, set ->
                val list = y.getOrPut(index) { mutableListOf() }
                list += processSet(set, index) ?: return@forEachIndexed
            }
        }

        return y.map { x to it }
    }

    protected abstract suspend fun processSet(set: ExerciseSet, setIndex: Int): Double?
}
