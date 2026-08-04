package com.patrykandpatrick.liftapp.domain.routine

fun interface ReorderRoutinesUseCase {

    suspend fun reorderRoutineIDs(routineIDs: List<Long>)
}

/** Stores the routines in the order in which their IDs appear. */
suspend operator fun ReorderRoutinesUseCase.invoke(routineIDs: List<Long>) {
    reorderRoutineIDs(routineIDs)
}
