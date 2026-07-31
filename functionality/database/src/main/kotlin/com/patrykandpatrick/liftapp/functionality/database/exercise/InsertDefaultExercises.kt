package com.patrykandpatrick.liftapp.functionality.database.exercise

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRepository
import javax.inject.Inject

class InsertDefaultExercises
@Inject
constructor(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke() {
        exerciseRepository.insert(DefaultExercises.exercises)
    }
}
