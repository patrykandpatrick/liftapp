package com.patrykandpatryk.liftapp.domain.routine

fun interface DeleteRoutineUseCase {
    suspend fun deleteRoutine(routineID: Long)
}

suspend operator fun DeleteRoutineUseCase.invoke(routineID: Long) = deleteRoutine(routineID)
