package com.patrykandpatrick.liftapp.domain.target

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.model.Name

/** One day of one cycle, as the editor needs it: the targets plus enough to name what they are. */
data class PlanDayTargets(
    val planID: Long,
    val cycleIndex: Int,
    val planItemOrderIndex: Int,
    val routineName: String?,
    val exercises: List<Item>,
) {
    data class Item(val name: Name, val exerciseType: ExerciseType, val target: ExerciseTarget)
}
