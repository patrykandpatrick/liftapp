package com.patrykandpatryk.liftapp.domain.goal

fun interface SaveGoalContract {
    suspend fun saveGoal(routineID: Long, exerciseID: Long, goal: Goal)
}
