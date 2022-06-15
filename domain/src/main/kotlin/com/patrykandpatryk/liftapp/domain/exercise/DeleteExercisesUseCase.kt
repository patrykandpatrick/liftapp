package com.patrykandpatryk.liftapp.domain.exercise

import javax.inject.Inject

class DeleteExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository,
) {

    suspend operator fun invoke(exerciseId: Long) {
        repository.delete(exerciseId)
    }
}
