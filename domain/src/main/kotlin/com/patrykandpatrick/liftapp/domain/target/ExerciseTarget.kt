package com.patrykandpatrick.liftapp.domain.target

import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import kotlin.time.Duration

/**
 * One exercise's prescription for one day of one cycle of one plan.
 *
 * The set count is the length of [sets] rather than a field of its own. That is what makes a volume
 * ramp — ten sets in week one, twenty by week five — something the plan can hold, and it removes
 * the state where a stored count and a stored list disagree.
 *
 * [restTime] and [distanceUnit] sit here rather than on [SetTarget]: neither varies set to set, and
 * a unit that could differ between two sets of the same exercise would be a bug, not a feature.
 */
data class ExerciseTarget(
    val planID: Long,
    val cycleIndex: Int,
    val planItemOrderIndex: Int,
    val exerciseID: Long,
    val sets: List<SetTarget>,
    val restTime: Duration,
    val distanceUnit: LongDistanceUnit = LongDistanceUnit.Kilometer,
) {
    val setCount: Int
        get() = sets.size
}
