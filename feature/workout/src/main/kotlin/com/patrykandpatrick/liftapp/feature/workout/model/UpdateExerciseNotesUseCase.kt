package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatrick.liftapp.domain.workout.UpdateExerciseNotesContract
import javax.inject.Inject

class UpdateExerciseNotesUseCase
@Inject
constructor(private val contract: UpdateExerciseNotesContract) {
    suspend operator fun invoke(exercise: EditableWorkout.Exercise, notes: String) {
        contract.updateExerciseNotes(
            workoutItemID = exercise.workoutItemID,
            exerciseID = exercise.id,
            notes = notes,
        )
    }
}
