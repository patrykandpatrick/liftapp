package com.patrykandpatrick.liftapp.domain.workout

import javax.inject.Inject

class GetPastWorkoutsUseCase
@Inject
constructor(private val getWorkoutsContract: GetWorkoutsContract) {
    /** @param limit how many workouts to read at most, newest first, or `null` for all of them. */
    operator fun invoke(limit: Int? = null) =
        getWorkoutsContract.getWorkouts(GetWorkoutsContract.WorkoutType.PAST, limit)
}
