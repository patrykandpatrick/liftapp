package com.patrykandpatryk.liftapp.domain.exercise

import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class InsertExercisesUseCase @Inject constructor(private val repository: ExerciseRepository) {

    suspend operator fun invoke(exercise: Exercise.Insert) =
        withContext(NonCancellable) { repository.insert(exercise) }
}
