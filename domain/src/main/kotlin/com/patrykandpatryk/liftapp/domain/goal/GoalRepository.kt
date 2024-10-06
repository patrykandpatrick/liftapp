package com.patrykandpatryk.liftapp.domain.goal

interface GoalRepository {
    suspend fun getGoal(routineID: Long, exerciseID: Long): Goal?

    suspend fun getDefaultGoal(exerciseID: Long): Goal?

    suspend fun saveGoal(routineID: Long, exerciseID: Long, goal: Goal)
}