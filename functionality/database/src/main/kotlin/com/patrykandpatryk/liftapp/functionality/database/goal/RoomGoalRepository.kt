package com.patrykandpatryk.liftapp.functionality.database.goal

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.goal.GetExerciseGoalContract
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.goal.SaveGoalContract
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

    override fun getGoal(routineID: Long, exerciseID: Long): Flow<Goal> =
        flow {
                val goal =
                    goalDao.getGoal(routineID, exerciseID)?.toDomain()
                        ?: goalDao.getDefaultGoal(exerciseID)?.goal
                        ?: Goal.Companion.Default
                emit(goal)
            }
            .flowOn(dispatcher)

    override suspend fun saveGoal(routineID: Long, exerciseID: Long, goal: Goal) =
        withContext(dispatcher) { goalDao.saveGoal(goal.toEntity(routineID, exerciseID)) }
}
