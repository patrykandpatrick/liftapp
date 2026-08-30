package com.patrykandpatrick.liftapp.functionality.database.target

import com.patrykandpatrick.liftapp.domain.di.IODispatcher
import com.patrykandpatrick.liftapp.domain.target.ExerciseTarget
import com.patrykandpatrick.liftapp.domain.target.TargetRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomTargetRepository
@Inject
constructor(
    private val targetDao: TargetDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : TargetRepository {

    override fun getTargets(
        planID: Long,
        cycleIndex: Int,
        planItemOrderIndex: Int,
    ): Flow<List<ExerciseTarget>> =
        targetDao
            .getTargets(planID, cycleIndex, planItemOrderIndex)
            .map { entities -> entities.map(ExerciseTargetEntity::toDomain) }
            .flowOn(dispatcher)

    override suspend fun upsertTargets(targets: List<ExerciseTarget>) {
        withContext(dispatcher) { targetDao.upsertTargets(targets.map(ExerciseTarget::toEntity)) }
    }
}
