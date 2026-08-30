package com.patrykandpatrick.liftapp.functionality.database.migration

import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The shape prescription had at schema 12, frozen here rather than followed from the domain.
 *
 * [Migration11To12] writes it into `exercise_goal` and the `goal` table, and [Migration14To15]
 * reads it back out to build targets. The live model has no equivalent — a target is a list of
 * sets, not one range and a count — so a migration that tracked the domain would rewrite history
 * every time the domain moved.
 *
 * Property names are the stored JSON. Renaming one is a silent data break.
 */
@Serializable
@SerialName("Goal")
data class LegacyGoal(
    val minReps: Int,
    val maxReps: Int,
    val sets: Int,
    val restTime: Duration,
    val duration: Duration,
    val distance: Double,
    val distanceUnit: LongDistanceUnit,
    val calories: Double,
    val id: Long = ID_NOT_SET,
) {
    companion object {
        val default =
            LegacyGoal(8, 12, 3, 2L.minutes, 5L.minutes, 0.5, LongDistanceUnit.Kilometer, 50.0)
    }
}
