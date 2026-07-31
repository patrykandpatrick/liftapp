package com.patrykandpatrick.liftapp.domain.goal

interface GoalRepository : GetExerciseGoalContract {
    suspend fun saveGoal(routineID: Long, exerciseID: Long, goal: Goal)
}
