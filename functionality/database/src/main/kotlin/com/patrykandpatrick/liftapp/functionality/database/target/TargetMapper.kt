package com.patrykandpatrick.liftapp.functionality.database.target

import com.patrykandpatrick.liftapp.domain.target.ExerciseTarget
import kotlin.time.Duration.Companion.milliseconds

fun ExerciseTargetEntity.toDomain(): ExerciseTarget =
    ExerciseTarget(
        planID = planID,
        cycleIndex = cycleIndex,
        planItemOrderIndex = itemOrderIndex,
        exerciseID = exerciseID,
        sets = sets,
        restTime = restTimeMillis.milliseconds,
        distanceUnit = distanceUnit,
    )

fun ExerciseTarget.toEntity(): ExerciseTargetEntity =
    ExerciseTargetEntity(
        planID = planID,
        cycleIndex = cycleIndex,
        itemOrderIndex = planItemOrderIndex,
        exerciseID = exerciseID,
        sets = sets,
        restTimeMillis = restTime.inWholeMilliseconds,
        distanceUnit = distanceUnit,
    )
