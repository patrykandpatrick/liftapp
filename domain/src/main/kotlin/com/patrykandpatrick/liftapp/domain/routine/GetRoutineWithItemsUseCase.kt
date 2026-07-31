package com.patrykandpatrick.liftapp.domain.routine

import kotlinx.coroutines.flow.Flow

fun interface GetRoutineWithItemsUseCase {
    fun getRoutineWithItems(routineID: Long): Flow<RoutineWithItems?>
}

operator fun GetRoutineWithItemsUseCase.invoke(routineID: Long) = getRoutineWithItems(routineID)
