package com.patrykandpatryk.liftapp.feature.newroutine.usecase

import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import javax.inject.Inject

class InsertRoutineUseCase @Inject constructor(
    private val routineRepository: RoutineRepository,
) {

    suspend operator fun invoke(
        name: String,
        exerciseIds: List<Long>,
    ): Long = routineRepository.insert(name, exerciseIds)
}
