package com.patrykandpatrick.liftapp.domain.workout

import kotlinx.coroutines.flow.Flow

fun interface GetWorkoutsContract {
    /** @param limit how many workouts to read at most, newest first, or `null` for all of them. */
    fun getWorkouts(type: WorkoutType, limit: Int?): Flow<List<Workout>>

    enum class WorkoutType {
        ACTIVE,
        PAST,
    }
}
