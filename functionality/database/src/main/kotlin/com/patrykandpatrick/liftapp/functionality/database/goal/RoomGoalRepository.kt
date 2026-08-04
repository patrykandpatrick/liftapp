package com.patrykandpatrick.liftapp.functionality.database.goal

import com.patrykandpatrick.liftapp.domain.di.IODispatcher
import com.patrykandpatrick.liftapp.domain.goal.GetExerciseGoalContract
import com.patrykandpatrick.liftapp.domain.goal.Goal
import com.patrykandpatrick.liftapp.domain.goal.SaveGoalContract
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class RoomGoalRepository
@Inject
constructor(
    private val goalDao: GoalDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : GetExerciseGoalContract, SaveGoalContract {

    override fun getGoal(routineID: Long, exerciseID: Long): Flow<Goal> = flow {
        val goal =
            goalDao.getGoal(routineID, exerciseID)?.toDomain()
                ?: goalDao.getDefaultGoal(exerciseID)?.goal
                ?: Goal.Companion.default
        emit(goal)
    }
        .flowOn(dispatcher)

    override suspend fun saveGoal(routineID: Long, exerciseID: Long, goal: Goal) {
        withContext(dispatcher) { goalDao.saveGoal(goal.toEntity(routineID, exerciseID)) }
    }
}
