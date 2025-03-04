package com.patrykandpatryk.liftapp.domain.workout

import javax.inject.Inject

class GetPastWorkoutsUseCase
@Inject
constructor(private val getWorkoutsContract: GetWorkoutsContract) {
    operator fun invoke() = getWorkoutsContract.getWorkouts(GetWorkoutsContract.WorkoutType.PAST)
}
