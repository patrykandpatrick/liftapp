package com.patrykandpatrick.liftapp.feature.workout.model

import androidx.compose.runtime.Immutable
import kotlin.time.Duration

@Immutable
data class RestTimerState(
    val remainingFraction: Float,
    val remainingDuration: Duration,
    val isPaused: Boolean,
    val workoutID: Long,
) {
    val isFinished: Boolean
        get() = remainingFraction == 0f

    companion object {
        fun finished(workoutID: Long) = RestTimerState(0f, Duration.ZERO, true, workoutID)
    }
}
