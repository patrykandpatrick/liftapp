package com.patrykandpatrick.liftapp.functionality.database.routine

import com.patrykandpatrick.liftapp.domain.di.IODispatcher
import com.patrykandpatrick.liftapp.domain.routine.DeleteRoutineUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithExercisesUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutinesWithExerciseNamesContract
import com.patrykandpatrick.liftapp.domain.routine.ReorderRoutinesUseCase
import com.patrykandpatrick.liftapp.domain.routine.Routine
import com.patrykandpatrick.liftapp.domain.routine.RoutineItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineRepository
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExerciseNames
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithItems
import com.patrykandpatrick.liftapp.domain.routine.UpsertRoutineUseCase
import com.patrykandpatrick.liftapp.domain.routine.UpsertRoutineWithItemsUseCase
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
    @param:IODispatcher private val dispatcher: CoroutineDispatcher,
) :
    RoutineRepository,
    GetRoutineWithExercisesUseCase,
    GetRoutineWithItemsUseCase,
    UpsertRoutineUseCase,
    UpsertRoutineWithItemsUseCase,
    GetRoutinesWithExerciseNamesContract,
    ReorderRoutinesUseCase,
    DeleteRoutineUseCase {

    override fun getRoutinesWithExerciseNames(): Flow<List<RoutineWithExerciseNames>> =
        routineDao.getRoutinesWithExerciseNames().map(routineMapper::toDomain).flowOn(dispatcher)

    override fun getRoutineWithItems(routineID: Long): Flow<RoutineWithItems?> {
        return getRoutineWithExercises(routineID)
            .map { routine ->
                routine?.let {
                    RoutineWithItems(
                        id = it.id,
                        name = it.name,
                        items =
                            it.items.map { item ->
                                RoutineItem(
                                    id = item.id,
                                    type = item.type,
                                    exerciseIDs = item.exercises.map { exercise -> exercise.id },
                                    supersetConfig = item.supersetConfig,
                                )
                            },
                    )
                }
            }
            .flowOn(dispatcher)
    }

    override fun getRoutineWithExercises(routineId: Long): Flow<RoutineWithExercises?> =
        combine(routineDao.getRoutine(routineId), routineDao.getRoutineItems(routineId)) {
                routine,
                items ->
                routine?.let { routineMapper.toDomain(it, items) }
            }
            .flowOn(dispatcher)

    override suspend fun upsert(routine: Routine): Long =
        withContext(dispatcher + NonCancellable) {
            routineDao.upsert(routine = routine.toEntity()).takeIf { it > 0 } ?: routine.id
        }

    override suspend fun upsert(routine: Routine, items: List<RoutineItem>): Long =
        withContext(dispatcher + NonCancellable) {
            routineDao.upsertWithItems(routine.toEntity(), items)
        }

    override suspend fun deleteRoutine(routineID: Long) {
        withContext(dispatcher + NonCancellable) { routineDao.delete(routineID) }
    }

    override suspend fun reorderRoutineIDs(routineIDs: List<Long>) {
        withContext(dispatcher + NonCancellable) { routineDao.reorder(routineIDs) }
    }

    override suspend fun delete(routineId: Long) {
        withContext(dispatcher) { routineDao.delete(routineId) }
    }
}
