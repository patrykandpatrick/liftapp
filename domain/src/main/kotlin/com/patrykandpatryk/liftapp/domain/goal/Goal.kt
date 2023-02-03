package com.patrykandpatryk.liftapp.domain.goal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.minutes

@Serializable
@SerialName("Goal")
data class Goal(
    val minReps: Int = 8,
    val maxReps: Int = 12,
    val sets: Int = 3,
    val breakDurationMillis: Long = 1L.minutes.inWholeMilliseconds,
) : java.io.Serializable
