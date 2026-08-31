package com.patrykandpatrick.liftapp.functionality.database.target

import com.patrykandpatrick.liftapp.domain.di.IODispatcher
import com.patrykandpatrick.liftapp.domain.target.ExerciseTarget
import com.patrykandpatrick.liftapp.domain.target.PlanDayTargets
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

    override fun getDayTargets(
        planID: Long,
        cycleIndex: Int,
        planItemOrderIndex: Int,
    ): Flow<PlanDayTargets> =
        targetDao
            .getTargetsWithExercises(planID, cycleIndex, planItemOrderIndex)
            .map { rows ->
                PlanDayTargets(
                    planID = planID,
                    cycleIndex = cycleIndex,
                    planItemOrderIndex = planItemOrderIndex,
                    routineName = rows.firstOrNull()?.routineName,
                    // One row per exercise is an invariant of the target table's primary key, so
                    // a repeat can only be a join fanning out. The screen keys a list on this;
                    // holding the invariant here keeps that a wrong order rather than a crash.
                    exercises =
                        rows
                            .distinctBy { it.target.exerciseID }
                            .map { row ->
                                PlanDayTargets.Item(
                                    name = row.exerciseName,
                                    exerciseType = row.exerciseType,
                                    target = row.target.toDomain(),
                                )
                            },
                )
            }
            .flowOn(dispatcher)

    override suspend fun upsertTargets(targets: List<ExerciseTarget>) {
        withContext(dispatcher) { targetDao.upsertTargets(targets.map(ExerciseTarget::toEntity)) }
    }

    override suspend fun addMissingTargets(targets: List<ExerciseTarget>) {
        withContext(dispatcher) {
            targetDao.insertMissingTargets(targets.map(ExerciseTarget::toEntity))
        }
    }
}
