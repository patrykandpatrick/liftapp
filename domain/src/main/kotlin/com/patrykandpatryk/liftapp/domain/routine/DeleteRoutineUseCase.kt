package com.patrykandpatryk.liftapp.domain.routine

import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class DeleteRoutineUseCase @Inject constructor(private val routineRepository: RoutineRepository) {

    suspend operator fun invoke(routineId: Long) =
        withContext(NonCancellable) { routineRepository.delete(routineId) }
}
