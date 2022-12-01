package com.patrykandpatryk.liftapp.domain.routine

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRoutineUseCase @Inject constructor(
    private val routineRepository: RoutineRepository,
) {

    operator fun invoke(routineId: Long): Flow<Routine?> =
        routineRepository.getRoutine(routineId)
}
