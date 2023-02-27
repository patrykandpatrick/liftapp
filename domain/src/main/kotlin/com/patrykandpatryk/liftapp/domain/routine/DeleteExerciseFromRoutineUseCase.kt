package com.patrykandpatryk.liftapp.domain.routine

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteExerciseFromRoutineUseCase @Inject constructor(
    private val routineRepository: RoutineRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(
        routineId: Long,
        exerciseId: Long,
    ) = withContext(dispatcher + NonCancellable) {
        routineRepository.deleteExerciseWithRoutine(routineId, exerciseId)
    }
}
