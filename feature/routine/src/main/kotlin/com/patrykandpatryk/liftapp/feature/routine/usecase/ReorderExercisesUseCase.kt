package com.patrykandpatryk.liftapp.feature.routine.usecase

import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import javax.inject.Inject

class ReorderExercisesUseCase
@Inject
constructor(private val routineRepository: RoutineRepository) {

    suspend operator fun invoke(routineId: Long, exercises: List<RoutineExerciseItem>) {
        routineRepository.reorderExercises(
            routineId = routineId,
            exerciseIds = exercises.map { it.id },
        )
    }
}
