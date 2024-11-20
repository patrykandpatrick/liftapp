package com.patrykandpatryk.liftapp.domain.workout

import javax.inject.Inject

class UpsertGoalSetsUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(
        workoutID: Long,
        exercise: Workout.Exercise,
        sets: Int,
    ) {
        workoutRepository.upsertWorkoutGoal(
            workoutID = workoutID,
            exerciseID = exercise.id,
            minReps = exercise.goal.minReps,
            maxReps = exercise.goal.maxReps,
            sets = sets,
            breakDurationMillis = exercise.goal.breakDuration.inWholeMilliseconds,
        )
    }
}
