package com.patrykandpatryk.liftapp.functionality.database.goal

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.goal.Goal
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class GoalMapper @Inject constructor()

fun GoalEntity.toDomain(): Goal =
    Goal(
        minReps = minReps,
        maxReps = maxReps,
        sets = sets,
        breakDuration = breakDurationMillis.milliseconds,
    )

fun Goal.toEntity(routineID: Long, exerciseID: Long): GoalEntity =
    GoalEntity(
        id = ID_NOT_SET,
        routineID = routineID,
        exerciseID = exerciseID,
        minReps = minReps,
        maxReps = maxReps,
        sets = sets,
        breakDurationMillis = breakDuration.inWholeMilliseconds,
    )
