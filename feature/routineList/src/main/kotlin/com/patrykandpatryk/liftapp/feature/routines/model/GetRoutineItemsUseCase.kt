package com.patrykandpatryk.liftapp.feature.routines.model

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.routine.GetRoutinesWithExerciseNamesContract
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetRoutineItemsUseCase
@Inject
constructor(
    private val contract: GetRoutinesWithExerciseNamesContract,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {

    operator fun invoke(): Flow<List<RoutineItem>> =
        contract
            .getRoutinesWithExerciseNames()
            .map(RoutineItem.Companion::create)
            .flowOn(dispatcher)
}
