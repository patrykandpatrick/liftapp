package com.patrykandpatryk.liftapp.domain.routine

fun interface UpsertRoutineWithExerciseIdsUseCase {
    suspend fun upsert(routine: Routine, exerciseIds: List<Long>): Long
}

suspend operator fun UpsertRoutineWithExerciseIdsUseCase.invoke(
    routine: Routine,
    exerciseIds: List<Long>,
) = upsert(routine, exerciseIds)
