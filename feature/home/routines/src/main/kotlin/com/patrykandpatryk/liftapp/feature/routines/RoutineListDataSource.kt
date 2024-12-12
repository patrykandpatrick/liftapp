package com.patrykandpatryk.liftapp.feature.routines

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class RoutineListDataSource(val routineItems: Flow<ImmutableList<RoutineItem>>) {
    @Inject
    constructor(
        routineRepository: RoutineRepository,
        @IODispatcher dispatcher: CoroutineDispatcher,
    ) : this(getRoutineItems(routineRepository, dispatcher))

    private companion object {
        fun getRoutineItems(
            routineRepository: RoutineRepository,
            dispatcher: CoroutineDispatcher,
        ): Flow<ImmutableList<RoutineItem>> =
            routineRepository.getRoutinesWithNames().map(RoutineItem::create).flowOn(dispatcher)
    }
}
