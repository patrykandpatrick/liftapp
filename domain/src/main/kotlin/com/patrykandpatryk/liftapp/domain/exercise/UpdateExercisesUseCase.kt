package com.patrykandpatryk.liftapp.domain.exercise

import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class UpdateExercisesUseCase @Inject constructor(private val repository: ExerciseRepository) {

    suspend operator fun invoke(exercise: Exercise.Update) =
        withContext(NonCancellable) { repository.update(exercise) }
}
