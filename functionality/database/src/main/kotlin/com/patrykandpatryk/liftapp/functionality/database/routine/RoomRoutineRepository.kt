package com.patrykandpatryk.liftapp.functionality.database.routine

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExerciseIDsContract
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExercisesContract
import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseIds
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseNames
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatryk.liftapp.domain.routine.UpsertRoutineWithExerciseIdsContract
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomRoutineRepository
@Inject
constructor(
    private val routineDao: RoutineDao,
    private val routineMapper: RoutineMapper,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) :
    RoutineRepository,
    GetRoutineWithExercisesContract,
    GetRoutineWithExerciseIDsContract,
    UpsertRoutineWithExerciseIdsContract {

    override fun getRoutinesWithNames(): Flow<List<RoutineWithExerciseNames>> =
        routineDao.getRoutinesWithExerciseNames().map(routineMapper::toDomain).flowOn(dispatcher)

    override fun getRoutineWithExerciseIDs(routineID: Long): Flow<RoutineWithExerciseIds?> =
        routineDao
            .getRoutineWithExerciseIds(routineID)
            .map { routine ->
                routine ?: return@map null
                RoutineWithExerciseIds(
                    id = routine.id,
                    name = routine.name,
                    exerciseIDs = routine.exerciseIDs.split(",").map(String::toLong),
                )
            }
            .flowOn(dispatcher)

    override fun getRoutineWithExercises(routineId: Long): Flow<RoutineWithExercises?> =
        combine(routineDao.getRoutine(routineId), routineDao.getRoutineExercises(routineId)) {
                nullableRoutine,
                exercisesWithGoals ->
                nullableRoutine?.let { routine ->
                    routineMapper.toDomain(routine, exercisesWithGoals)
                }
            }
            .flowOn(dispatcher)

    override suspend fun upsert(routine: Routine, exerciseIds: List<Long>): Long =
        withContext(dispatcher + NonCancellable) {
            var routineId = routineDao.upsert(routine = routine.toEntity())

            routineId = routineId.takeIf { it > 0 } ?: routine.id

            upsertOrderedExercises(routineId, exerciseIds)

            routineDao.deleteExerciseWithRoutinesNotIn(routineId, exerciseIds)

            routineId
        }

    override suspend fun reorderExercises(routineId: Long, exerciseIds: List<Long>) {
        upsertOrderedExercises(routineId, exerciseIds)
    }

    private suspend fun upsertOrderedExercises(routineId: Long, exerciseIds: List<Long>) {
        exerciseIds
            .mapIndexed { index, id ->
                ExerciseWithRoutineEntity(
                    routineId = routineId,
                    exerciseId = id,
                    orderIndex = index,
                )
            }
            .also { exerciseWithRoutineEntities -> routineDao.upsert(exerciseWithRoutineEntities) }
    }

    override suspend fun delete(routineId: Long) {
        routineDao.delete(routineId)
    }

    override suspend fun deleteExerciseWithRoutine(routineId: Long, exerciseId: Long) {
        routineDao.deleteExerciseWithRoutine(routineId, exerciseId)
    }
}
