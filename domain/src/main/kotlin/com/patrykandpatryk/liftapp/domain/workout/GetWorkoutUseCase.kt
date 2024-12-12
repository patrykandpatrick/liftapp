package com.patrykandpatryk.liftapp.domain.workout

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetWorkoutUseCase @Inject constructor(private val workoutRepository: WorkoutRepository) {
    operator fun invoke(routineID: Long, workoutID: Long?): Flow<Workout> =
        workoutRepository.getWorkout(routineID, workoutID)
}
