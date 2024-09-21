package com.patrykandpatryk.liftapp.feature.newroutine.usecase

import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRoutineWithExerciseIdsUseCase @Inject constructor(
    private val routineRepository: RoutineRepository,
) {

    operator fun invoke(routineId: Long): Flow<RoutineWithExercises?> =
        routineRepository.getRoutineWithExercises(routineId)
}
