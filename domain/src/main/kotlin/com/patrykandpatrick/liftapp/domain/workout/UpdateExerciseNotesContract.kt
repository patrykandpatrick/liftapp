package com.patrykandpatrick.liftapp.domain.workout

fun interface UpdateExerciseNotesContract {
    suspend fun updateExerciseNotes(workoutItemID: Long, exerciseID: Long, notes: String)
}
