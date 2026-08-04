package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatrick.liftapp.domain.extension.getOrPut
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class AverageSpeedExerciseSetSummaryMapper
@Inject
constructor(private val unitConverter: UnitConverter) : ExerciseSetSummaryMapper {
    override suspend fun invoke(
        input: List<ExerciseSetGroup>
    ): List<Pair<List<Double>, List<Double>>> {
        val x = mutableListOf<Double>()
        val y = mutableListOf<MutableList<Double>>()

        input.forEachIndexed { index, group ->
            x += group.workoutStartDate.toLocalDate().toEpochDay().toDouble()
            group.sets.forEachIndexed { index, set ->
                val list = y.getOrPut(index) { mutableListOf() }
                list +=
                    when (set) {
                        is ExerciseSet.Cardio -> {
                            val distance =
                                unitConverter.convertToPreferredUnit(set.distanceUnit, set.distance)
                            distance /
                                (set.duration.inWholeMilliseconds /
                                    1.hours.inWholeMilliseconds.toDouble())
                        }

                        is ExerciseSet.Calisthenics,
                        is ExerciseSet.Reps,
                        is ExerciseSet.Time,
                        is ExerciseSet.Weight -> return@forEachIndexed
                    }
            }
        }

        return y.map { x to it }
    }
}
