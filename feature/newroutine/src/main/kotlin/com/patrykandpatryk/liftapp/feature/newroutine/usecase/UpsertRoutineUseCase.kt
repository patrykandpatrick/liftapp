package com.patrykandpatryk.liftapp.feature.newroutine.usecase

import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import javax.inject.Inject

class UpsertRoutineUseCase @Inject constructor(private val routineRepository: RoutineRepository) {

    suspend operator fun invoke(id: Long, name: String, exerciseIds: List<Long>): Long =
        routineRepository.upsert(routine = Routine(id = id, name = name), exerciseIds = exerciseIds)
}
