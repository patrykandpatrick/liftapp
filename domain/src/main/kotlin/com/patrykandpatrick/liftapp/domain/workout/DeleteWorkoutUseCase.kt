package com.patrykandpatrick.liftapp.domain.workout

import javax.inject.Inject

/**
 * Removes a workout along with everything recorded under it. The items, goals and sets all name the
 * workout with `ON DELETE CASCADE`, so the one statement is the whole deletion.
 */
class DeleteWorkoutUseCase
@Inject
constructor(private val deleteWorkoutContract: DeleteWorkoutContract) {
    suspend operator fun invoke(workoutID: Long) {
        deleteWorkoutContract.deleteWorkout(workoutID)
    }
}
