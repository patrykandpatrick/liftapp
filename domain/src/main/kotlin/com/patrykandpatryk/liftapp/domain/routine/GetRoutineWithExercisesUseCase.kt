package com.patrykandpatryk.liftapp.domain.routine

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetRoutineWithExercisesUseCase
@Inject
constructor(private val routineRepository: RoutineRepository) {

    operator fun invoke(routineId: Long): Flow<RoutineWithExercises?> =
        routineRepository.getRoutineWithExercises(routineId)
}
