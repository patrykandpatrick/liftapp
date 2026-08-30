package com.patrykandpatrick.liftapp.functionality.database.target

import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.target.ExerciseTarget
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.target.TargetTemplate
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.workout.Workout
import kotlin.time.Duration

/**
 * Flattens a target into the per-workout snapshot `workout_goal` still holds.
 *
 * The snapshot is one rep range for the whole exercise, so a target whose sets differ loses that
 * difference here — deliberately, until the workout screen can show a target per set. The range
 * shown is the widest any set asks for, which is the only summary that cannot contradict a set.
 */
fun ExerciseTarget.toWorkoutGoal(): Workout.Goal = workoutGoal(sets, restTime, distanceUnit)

fun TargetTemplate.toWorkoutGoal(
    distanceUnit: LongDistanceUnit = LongDistanceUnit.Kilometer
): Workout.Goal = workoutGoal(sets, restTime, distanceUnit)

private fun workoutGoal(
    sets: List<SetTarget>,
    restTime: Duration,
    distanceUnit: LongDistanceUnit,
): Workout.Goal {
    val first = sets.firstOrNull()
    return Workout.Goal(
        id = ID_NOT_SET,
        minReps = sets.minOfOrNull { it.minReps } ?: 0,
        maxReps = sets.maxOfOrNull { it.maxReps } ?: 0,
        sets = sets.size,
        restTime = restTime,
        duration = first?.duration ?: Duration.ZERO,
        distance = first?.distance ?: 0.0,
        distanceUnit = distanceUnit,
        calories = first?.calories ?: 0.0,
    )
}
