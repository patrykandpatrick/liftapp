package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatryk.liftapp.domain.workout.UpsertWorkoutGoalContract
import javax.inject.Inject

class UpsertGoalSetsUseCase @Inject constructor(private val contract: UpsertWorkoutGoalContract) {
    suspend operator fun invoke(
        workoutID: Long,
        exercise: EditableWorkout.Exercise,
        setCount: Int,
    ) {
        contract.upsertWorkoutGoal(
            workoutID = workoutID,
            exerciseID = exercise.id,
            goal = exercise.goal.copy(sets = setCount),
        )
    }
}
