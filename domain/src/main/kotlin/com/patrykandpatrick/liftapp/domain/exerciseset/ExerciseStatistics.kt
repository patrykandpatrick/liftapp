package com.patrykandpatrick.liftapp.domain.exerciseset

import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import kotlin.time.Duration

sealed interface ExerciseStatistics {
    data class Weight(
        val totalVolume: Double,
        val totalReps: Int,
        val minimumWeight: Double,
        val maximumWeight: Double,
        val massUnit: MassUnit,
    ) : ExerciseStatistics

    data class Reps(
        val totalReps: Int,
        val minimumReps: Int,
        val maximumReps: Int,
    ) : ExerciseStatistics

    data class Time(
        val totalDuration: Duration,
        val minimumDuration: Duration,
        val maximumDuration: Duration,
    ) : ExerciseStatistics

    data class Cardio(
        val totalDuration: Duration,
        val minimumDuration: Duration,
        val maximumDuration: Duration,
        val totalDistance: Double,
        val distanceUnit: LongDistanceUnit,
    ) : ExerciseStatistics
}
