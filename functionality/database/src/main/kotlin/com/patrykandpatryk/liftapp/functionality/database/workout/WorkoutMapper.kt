package com.patrykandpatryk.liftapp.functionality.database.workout

import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.workout.Workout
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class WorkoutMapper @Inject constructor() {
    fun toDomain(workoutEntity: WorkoutEntity, exercises: List<WorkoutExerciseDto>) =
        Workout(
            id = workoutEntity.id,
            name = workoutEntity.name,
            date = workoutEntity.date,
            duration = workoutEntity.durationMillis.milliseconds,
            notes = workoutEntity.notes,
            exercises = exercises.map { it.toDomain() },
        )

    private fun WorkoutExerciseDto.toDomain(): Workout.Exercise {
        val goal = goal?.toDomain() ?: exercise.goal
        return Workout.Exercise(
            id = exercise.id,
            name = exercise.name,
            exerciseType = exercise.exerciseType,
            mainMuscles = exercise.mainMuscles,
            secondaryMuscles = exercise.secondaryMuscles,
            tertiaryMuscles = exercise.tertiaryMuscles,
            goal = goal,
        )
    }
}

fun WorkoutGoalEntity.toDomain(): Goal =
    Goal(
        minReps = minReps,
        maxReps = maxReps,
        sets = sets,
        breakDuration = breakDurationMillis.milliseconds,
    )
