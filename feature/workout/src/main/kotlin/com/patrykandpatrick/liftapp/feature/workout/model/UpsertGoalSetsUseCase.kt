package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatryk.liftapp.domain.workout.UpsertWorkoutGoalContract
import javax.inject.Inject

class UpsertGoalSetsUseCase @Inject constructor(private val contract: UpsertWorkoutGoalContract) {
    // TODO update when goals use ids
    suspend operator fun invoke(workoutID: Long, exercise: EditableWorkout.Exercise, delta: Int) {
        contract.upsertWorkoutGoal(
            workoutID = workoutID,
            exerciseID = exercise.id,
            minReps = exercise.goal.minReps,
            maxReps = exercise.goal.maxReps,
            sets = (exercise.goal.sets + delta).coerceAtLeast(1),
            restTimeMillis = exercise.goal.restTime.inWholeMilliseconds,
            durationMillis = exercise.goal.duration.inWholeMilliseconds,
            distance = exercise.goal.distance,
            distanceUnit = exercise.goal.distanceUnit,
            calories = exercise.goal.calories,
        )
    }
}
