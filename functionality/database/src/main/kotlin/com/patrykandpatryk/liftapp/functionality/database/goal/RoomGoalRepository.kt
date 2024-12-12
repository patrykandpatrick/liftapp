package com.patrykandpatryk.liftapp.functionality.database.goal

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.goal.GoalRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RoomGoalRepository
@Inject
constructor(
    private val goalDao: GoalDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : GoalRepository {

    override suspend fun getGoal(routineID: Long, exerciseID: Long): Goal? =
        withContext(dispatcher) { goalDao.getGoal(routineID, exerciseID)?.toDomain() }

    override suspend fun getDefaultGoal(exerciseID: Long): Goal? =
        withContext(dispatcher) { goalDao.getDefaultGoal(exerciseID)?.goal }

    override suspend fun saveGoal(routineID: Long, exerciseID: Long, goal: Goal) =
        withContext(dispatcher) { goalDao.saveGoal(goal.toEntity(routineID, exerciseID)) }
}
