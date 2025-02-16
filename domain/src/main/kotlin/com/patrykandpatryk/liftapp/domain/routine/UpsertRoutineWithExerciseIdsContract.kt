package com.patrykandpatryk.liftapp.domain.routine

fun interface UpsertRoutineWithExerciseIdsContract {
    suspend fun upsert(routine: Routine, exerciseIds: List<Long>): Long
}
