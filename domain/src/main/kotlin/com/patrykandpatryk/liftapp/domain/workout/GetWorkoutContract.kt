package com.patrykandpatryk.liftapp.domain.workout

import kotlinx.coroutines.flow.Flow

fun interface GetWorkoutContract {
    fun getWorkout(routineID: Long, workoutID: Long?): Flow<Workout>
}
