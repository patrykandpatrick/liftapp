package com.patrykandpatrick.liftapp.functionality.database.target

import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.target.ExerciseTarget
import com.patrykandpatrick.liftapp.domain.target.TargetTemplate
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.workout.Workout

/** Copies a target into the per-workout snapshot, set for set. */
fun ExerciseTarget.toWorkoutGoal(): Workout.Goal =
    Workout.Goal(
        id = ID_NOT_SET,
        setTargets = sets,
        restTime = restTime,
        distanceUnit = distanceUnit,
    )

fun TargetTemplate.toWorkoutGoal(
    distanceUnit: LongDistanceUnit = LongDistanceUnit.Kilometer
): Workout.Goal =
    Workout.Goal(
        id = ID_NOT_SET,
        setTargets = sets,
        restTime = restTime,
        distanceUnit = distanceUnit,
    )
