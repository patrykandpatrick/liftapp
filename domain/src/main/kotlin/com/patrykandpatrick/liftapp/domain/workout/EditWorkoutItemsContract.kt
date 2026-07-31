package com.patrykandpatrick.liftapp.domain.workout

interface EditWorkoutItemsContract {
    suspend fun addExercises(workoutID: Long, exerciseIDs: List<Long>)

    suspend fun reorderItems(workoutID: Long, workoutItemIDs: List<Long>)

    suspend fun removeItem(workoutID: Long, workoutItemID: Long)

    suspend fun updateSetCount(workoutID: Long, workoutItemID: Long, setCount: Int)
}
