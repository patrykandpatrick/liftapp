package com.patrykandpatryk.liftapp.domain.routine

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteRoutineUseCase @Inject constructor(
    private val routineRepository: RoutineRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(routineId: Long) = withContext(dispatcher + NonCancellable) {
        routineRepository.delete(routineId)
    }
}
