package com.patrykandpatryk.liftapp.domain.exercise

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAllExercisesUseCase @Inject constructor(private val repository: ExerciseRepository) {

    operator fun invoke(): Flow<List<Exercise>> = repository.getAllExercises()
}
