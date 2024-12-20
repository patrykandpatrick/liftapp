package com.patrykandpatryk.liftapp.functionality.database.goal

import com.patrykandpatryk.liftapp.domain.goal.Goal
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class GoalMapper @Inject constructor()

fun GoalEntity.toDomain(): Goal =
    Goal(
        id = id,
        minReps = minReps,
        maxReps = maxReps,
        sets = sets,
        breakDuration = breakDurationMillis.milliseconds,
    )

fun Goal.toEntity(routineID: Long, exerciseID: Long): GoalEntity =
    GoalEntity(
        id = id,
        routineID = routineID,
        exerciseID = exerciseID,
        minReps = minReps,
        maxReps = maxReps,
        sets = sets,
        breakDurationMillis = breakDuration.inWholeMilliseconds,
    )
