package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatryk.liftapp.domain.workout.UpdateWorkoutContract
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateWorkoutUseCase @Inject constructor(private val contract: UpdateWorkoutContract) {
    suspend operator fun invoke(
        workoutID: Long,
        name: String? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        notes: String? = null,
    ) {
        contract.updateWorkout(workoutID, name, startDate, endDate, notes)
    }
}
