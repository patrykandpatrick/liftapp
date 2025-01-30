package com.patrykandpatryk.liftapp.domain.workout

import kotlinx.coroutines.flow.Flow

fun interface GetWorkoutsContract {
    fun getWorkouts(type: WorkoutType): Flow<List<Workout>>

    enum class WorkoutType {
        ACTIVE,
        PAST,
    }
}
