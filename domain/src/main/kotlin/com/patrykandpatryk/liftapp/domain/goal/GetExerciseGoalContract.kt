package com.patrykandpatryk.liftapp.domain.goal

import kotlinx.coroutines.flow.Flow

fun interface GetExerciseGoalContract {
    fun getGoal(routineID: Long, exerciseID: Long): Flow<Goal>
}
