package com.patrykandpatrick.liftapp.domain.routine

fun interface UpsertRoutineWithItemsUseCase {
    suspend fun upsert(routine: Routine, items: List<RoutineItem>): Long
}

suspend operator fun UpsertRoutineWithItemsUseCase.invoke(
    routine: Routine,
    items: List<RoutineItem>,
) = upsert(routine, items)
