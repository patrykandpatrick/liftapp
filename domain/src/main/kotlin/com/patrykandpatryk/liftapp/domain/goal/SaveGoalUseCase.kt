package com.patrykandpatryk.liftapp.domain.goal

import javax.inject.Inject

class SaveGoalUseCase @Inject constructor(private val goalRepository: GoalRepository) {

    suspend operator fun invoke(routineID: Long, exerciseID: Long, goal: Goal) {
        goalRepository.saveGoal(routineID, exerciseID, goal)
    }
}
