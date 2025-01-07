package com.patrykandpatryk.liftapp.domain.goal

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Goal")
data class Goal(
    val minReps: Int,
    val maxReps: Int,
    val sets: Int,
    val restTime: Duration,
    val duration: Duration,
    val distance: Double,
    val distanceUnit: LongDistanceUnit,
    val calories: Double,
    val id: Long = ID_NOT_SET,
) : java.io.Serializable {
    companion object {
        val default = Goal(8, 12, 3, 2L.minutes, 5L.minutes, 0.5, LongDistanceUnit.Kilometer, 50.0)
        val repRange = 1.0..100.0
        val setRange = 1.0..100.0
    }
}
