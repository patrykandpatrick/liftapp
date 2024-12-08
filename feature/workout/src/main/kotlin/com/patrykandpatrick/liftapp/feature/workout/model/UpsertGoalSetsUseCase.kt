package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatryk.liftapp.domain.workout.UpsertWorkoutGoalContract
import javax.inject.Inject

class UpsertGoalSetsUseCase @Inject constructor(
    private val contract: UpsertWorkoutGoalContract,
) {
    suspend operator fun invoke(
        workoutID: Long,
        exercise: EditableWorkout.Exercise,
        delta: Int,
    ) {
        contract.upsertWorkoutGoal(
            workoutID = workoutID,
            exerciseID = exercise.id,
            minReps = exercise.goal.minReps,
            maxReps = exercise.goal.maxReps,
            sets = (exercise.goal.sets + delta).coerceAtLeast(1),
            breakDurationMillis = exercise.goal.breakDuration.inWholeMilliseconds,
        )
    }
}
