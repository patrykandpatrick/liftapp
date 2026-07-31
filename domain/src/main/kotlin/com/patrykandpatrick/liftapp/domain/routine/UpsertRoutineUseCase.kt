package com.patrykandpatrick.liftapp.domain.routine

fun interface UpsertRoutineUseCase {
    suspend fun upsert(routine: Routine): Long
}

suspend operator fun UpsertRoutineUseCase.invoke(routine: Routine) = upsert(routine)
