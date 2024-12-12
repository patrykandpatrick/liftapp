package com.patrykandpatryk.liftapp.feature.newroutine.usecase

import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetRoutineWithExerciseIdsUseCase
@Inject
constructor(private val routineRepository: RoutineRepository) {

    operator fun invoke(routineId: Long): Flow<RoutineWithExercises?> =
        routineRepository.getRoutineWithExercises(routineId)
}
