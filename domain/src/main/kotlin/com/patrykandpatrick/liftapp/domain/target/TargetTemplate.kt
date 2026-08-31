package com.patrykandpatrick.liftapp.domain.target

import kotlin.time.Duration

/**
 * A prescription with no plan position yet — what [TargetDefaults] and the generator both return.
 */
data class TargetTemplate(val sets: List<SetTarget>, val restTime: Duration) {
    fun at(
        planID: Long,
        cycleIndex: Int,
        planItemOrderIndex: Int,
        exerciseID: Long,
    ): ExerciseTarget =
        ExerciseTarget(
            planID = planID,
            cycleIndex = cycleIndex,
            planItemOrderIndex = planItemOrderIndex,
            exerciseID = exerciseID,
            sets = sets,
            restTime = restTime,
        )
}
