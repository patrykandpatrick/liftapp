package com.patrykandpatryk.liftapp.domain.goal

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
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
    val breakDuration: Duration,
    val id: Long = ID_NOT_SET,
) : java.io.Serializable {
    companion object {
        val Default = Goal(8, 12, 3, 1L.minutes)

        val RepRange = 1.0..100.0
        val SetRange = 1.0..100.0
    }
}
