package com.patrykandpatryk.liftapp.domain.goal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
) {
    operator fun invoke(routineID: Long, exerciseID: Long): Flow<Goal> =
        flow {
            val goal = goalRepository.getGoal(routineID, exerciseID)
                ?: goalRepository.getDefaultGoal(exerciseID)
                ?: Goal.Default
            emit(goal)
        }
}