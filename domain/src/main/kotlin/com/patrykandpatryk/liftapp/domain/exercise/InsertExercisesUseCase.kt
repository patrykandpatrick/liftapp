package com.patrykandpatryk.liftapp.domain.exercise

import javax.inject.Inject

class InsertExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository,
) {

    suspend operator fun invoke(exercise: Exercise.Insert) {
        repository.insert(exercise)
    }
}
