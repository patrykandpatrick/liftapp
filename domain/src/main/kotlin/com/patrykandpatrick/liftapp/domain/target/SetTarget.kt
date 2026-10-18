package com.patrykandpatrick.liftapp.domain.target

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * What one set is meant to be, as authored in the plan.
 *
 * Every field is prescription, never a reading: [ExerciseSet][com.patrykandpatrick.liftapp.domain
 * .workout.ExerciseSet] is what happened. A set the user logs is compared against this, not merged
 * into it.
 *
 * [rir] and [loadPercent] are nullable because a target may leave them open, which is not the same
 * as prescribing zero — zero RIR is a set taken to failure.
 *
 * [loadPercent] is a percentage of the estimated one-rep max, 70.0 for 70%.
 */
@Serializable
@SerialName("SetTarget")
data class SetTarget(
    val minReps: Int,
    val maxReps: Int,
    val rir: Int? = null,
    val duration: Duration = 0.minutes,
    val distance: Double = 0.0,
    val calories: Double = 0.0,
    val loadPercent: Double? = null,
) {
    companion object {
        val repRange = 1.0..100.0

        /** Bounds the length of an [ExerciseTarget]'s list, which is what a set count now is. */
        val setRange = 1.0..100.0
    }
}
