package com.patrykandpatryk.liftapp.functionality.database.goal

import com.patrykandpatryk.liftapp.domain.goal.Goal
import javax.inject.Inject

class GoalMapper @Inject constructor()

fun GoalEntity.toDomain(): Goal =
    Goal(
        minReps = minReps,
        maxReps = maxReps,
        sets = sets,
        breakDurationMillis = breakDurationMillis,
    )
