package com.patrykandpatryk.liftapp.domain.workout

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    operator fun invoke(routineID: Long, workoutID: Long?): Flow<Workout> =
        workoutRepository.getWorkout(routineID, workoutID)
}
