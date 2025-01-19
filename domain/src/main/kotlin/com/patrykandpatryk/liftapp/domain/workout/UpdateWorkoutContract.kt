package com.patrykandpatryk.liftapp.domain.workout

import java.time.LocalDateTime

interface UpdateWorkoutContract {
    suspend fun updateWorkout(
        workoutID: Long,
        name: String? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        notes: String? = null,
    )
}
