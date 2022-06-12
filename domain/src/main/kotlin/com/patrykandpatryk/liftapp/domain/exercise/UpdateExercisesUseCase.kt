package com.patrykandpatryk.liftapp.domain.exercise

import javax.inject.Inject

class UpdateExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository,
) {

    suspend operator fun invoke(exercise: Exercise.Update) {
        repository.update(exercise)
    }
}
