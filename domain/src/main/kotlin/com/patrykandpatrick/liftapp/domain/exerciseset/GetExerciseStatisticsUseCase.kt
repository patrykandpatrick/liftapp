package com.patrykandpatrick.liftapp.domain.exerciseset

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject

class GetExerciseStatisticsUseCase @Inject constructor(private val unitConverter: UnitConverter) {

    suspend operator fun invoke(
        exerciseType: ExerciseType,
        groups: List<ExerciseSetGroup>,
    ): ExerciseStatistics? {
        val sets = groups.flatMap(ExerciseSetGroup::sets).filter(ExerciseSet::isCompleted)
        if (sets.isEmpty()) return null

        return when (exerciseType) {
            ExerciseType.Weight,
            ExerciseType.Calisthenics -> getWeightStatistics(sets)
            ExerciseType.Reps -> getRepsStatistics(sets)
            ExerciseType.Time -> getTimeStatistics(sets)
            ExerciseType.Cardio -> getCardioStatistics(sets)
        }
    }

    private suspend fun getWeightStatistics(sets: List<ExerciseSet>): ExerciseStatistics.Weight? {
        val massUnit = unitConverter.getPreferredUnit(MassUnit.Kilograms)
        val values = sets.mapNotNull { set ->
            val (weight, weightUnit, reps) =
                when (set) {
                    is ExerciseSet.Weight -> Triple(set.weight, set.weightUnit, set.reps)
                    is ExerciseSet.Calisthenics ->
                        Triple(set.weight + set.bodyWeight, set.weightUnit, set.reps)
                    is ExerciseSet.Cardio,
                    is ExerciseSet.Reps,
                    is ExerciseSet.Time -> return@mapNotNull null
                }
            val convertedWeight = unitConverter.convertToPreferredUnit(weightUnit, weight)
            convertedWeight to reps
        }
        if (values.isEmpty()) return null

        return ExerciseStatistics.Weight(
            totalVolume = values.sumOf { (weight, reps) -> weight * reps },
            totalReps = values.sumOf { (_, reps) -> reps },
            minimumWeight = values.minOf { (weight) -> weight },
            maximumWeight = values.maxOf { (weight) -> weight },
            massUnit = massUnit,
        )
    }

    private fun getRepsStatistics(sets: List<ExerciseSet>): ExerciseStatistics.Reps? {
        val reps = sets.mapNotNull { (it as? ExerciseSet.Reps)?.reps }
        if (reps.isEmpty()) return null

        return ExerciseStatistics.Reps(
            totalReps = reps.sum(),
            minimumReps = reps.min(),
            maximumReps = reps.max(),
        )
    }

    private fun getTimeStatistics(sets: List<ExerciseSet>): ExerciseStatistics.Time? {
        val durations = sets.mapNotNull { (it as? ExerciseSet.Time)?.duration }
        if (durations.isEmpty()) return null

        return ExerciseStatistics.Time(
            totalDuration = durations.fold(kotlin.time.Duration.ZERO, kotlin.time.Duration::plus),
            minimumDuration = durations.min(),
            maximumDuration = durations.max(),
        )
    }

    private suspend fun getCardioStatistics(sets: List<ExerciseSet>): ExerciseStatistics.Cardio? {
        val cardioSets = sets.filterIsInstance<ExerciseSet.Cardio>()
        if (cardioSets.isEmpty()) return null

        val distanceUnit = unitConverter.getPreferredUnit(LongDistanceUnit.Kilometer)
        val durations = cardioSets.map(ExerciseSet.Cardio::duration)
        val totalDistance = cardioSets.sumOf { set ->
            unitConverter.convertToPreferredUnit(set.distanceUnit, set.distance)
        }

        return ExerciseStatistics.Cardio(
            totalDuration = durations.fold(kotlin.time.Duration.ZERO, kotlin.time.Duration::plus),
            minimumDuration = durations.min(),
            maximumDuration = durations.max(),
            totalDistance = totalDistance,
            distanceUnit = distanceUnit,
        )
    }
}
