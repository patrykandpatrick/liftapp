package com.patrykandpatrick.liftapp.domain.target

import kotlinx.coroutines.flow.Flow

fun interface GetExerciseTargetsContract {
    fun getTargets(
        planID: Long,
        cycleIndex: Int,
        planItemOrderIndex: Int,
    ): Flow<List<ExerciseTarget>>
}
