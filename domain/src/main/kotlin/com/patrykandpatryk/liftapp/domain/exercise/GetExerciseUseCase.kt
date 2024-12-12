package com.patrykandpatryk.liftapp.domain.exercise

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetExerciseUseCase @Inject constructor(private val repository: ExerciseRepository) {

    operator fun invoke(id: Long): Flow<Exercise?> = repository.getExercise(id)
}
