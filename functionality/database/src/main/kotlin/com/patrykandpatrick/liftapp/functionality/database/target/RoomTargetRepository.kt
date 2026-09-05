package com.patrykandpatrick.liftapp.functionality.database.target

import com.patrykandpatrick.liftapp.domain.di.IODispatcher
import com.patrykandpatrick.liftapp.domain.muscle.GetPlannedCycleVolumeContract
import com.patrykandpatrick.liftapp.domain.muscle.PlannedExerciseVolume
import com.patrykandpatrick.liftapp.domain.target.ExerciseTarget
import com.patrykandpatrick.liftapp.domain.target.PlanDayTargets
import com.patrykandpatrick.liftapp.domain.target.TargetRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomTargetRepository
@Inject
constructor(
    private val targetDao: TargetDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : TargetRepository, GetPlannedCycleVolumeContract {

    override fun getPlannedCycleVolume(
        planID: Long,
        cycleIndex: Int,
    ): Flow<List<PlannedExerciseVolume>> =
        targetDao
            .getCycleTargetsWithMuscles(planID, cycleIndex)
            .map { rows ->
                rows.map { row ->
                    PlannedExerciseVolume(
                        setCount = row.sets.size,
                        exerciseType = row.exerciseType,
                        primaryMuscles = row.mainMuscles,
                        secondaryMuscles = row.secondaryMuscles,
                        tertiaryMuscles = row.tertiaryMuscles,
                    )
                }
            }
            .flowOn(dispatcher)

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
        combine(
                targetDao.getTargetsWithExercises(planID, cycleIndex, planItemOrderIndex),
                targetDao.getCycleCount(planID),
            ) { rows, cycleCount ->
                PlanDayTargets(
                    planID = planID,
                    cycleIndex = cycleIndex,
                    planItemOrderIndex = planItemOrderIndex,
                    routineName = rows.firstOrNull()?.routineName,
                    cycleCount = cycleCount,
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
