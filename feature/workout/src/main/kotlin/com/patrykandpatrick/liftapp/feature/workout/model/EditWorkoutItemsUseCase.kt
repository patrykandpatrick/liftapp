package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatrick.liftapp.domain.workout.EditWorkoutItemsContract
import javax.inject.Inject

class EditWorkoutItemsUseCase @Inject constructor(private val contract: EditWorkoutItemsContract) {
    suspend fun addExercises(workoutID: Long, exerciseIDs: List<Long>) {
        contract.addExercises(workoutID, exerciseIDs)
    }

    suspend fun reorderItems(workoutID: Long, workoutItemIDs: List<Long>) {
        contract.reorderItems(workoutID, workoutItemIDs)
    }

    suspend fun removeItem(workoutID: Long, workoutItemID: Long) {
        contract.removeItem(workoutID, workoutItemID)
    }

    suspend fun updateSetCount(workoutID: Long, workoutItemID: Long, setCount: Int) {
        contract.updateSetCount(workoutID, workoutItemID, setCount)
    }
}
