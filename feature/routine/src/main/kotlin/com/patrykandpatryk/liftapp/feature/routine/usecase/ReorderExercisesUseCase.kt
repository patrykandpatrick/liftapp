package com.patrykandpatryk.liftapp.feature.routine.usecase

import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import com.patrykandpatryk.liftapp.feature.routine.model.ExerciseItem
import javax.inject.Inject

class ReorderExercisesUseCase @Inject constructor(
    private val routineRepository: RoutineRepository,
) {

    suspend operator fun invoke(routineId: Long, exercises: List<ExerciseItem>) {
        routineRepository.reorderExercises(
            routineId = routineId,
            exerciseIds = exercises.map { it.id },
        )
    }
}
