package com.patrykandpatryk.liftapp.domain.routine

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetRoutinesWithExerciseNamesUseCase
@Inject
constructor(
    private val routineRepository: RoutineRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {

    operator fun invoke(): Flow<List<RoutineWithExerciseNames>> =
        routineRepository.getRoutinesWithNames().flowOn(dispatcher)
}
