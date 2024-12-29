package com.patrykandpatryk.liftapp.domain.workout

import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit

fun interface UpsertWorkoutGoalContract {
    suspend fun upsertWorkoutGoal(
        workoutID: Long,
        exerciseID: Long,
        minReps: Int,
        maxReps: Int,
        sets: Int,
        restTimeMillis: Long,
        durationMillis: Long,
        distance: Double,
        distanceUnit: LongDistanceUnit,
        calories: Double,
    )
}
