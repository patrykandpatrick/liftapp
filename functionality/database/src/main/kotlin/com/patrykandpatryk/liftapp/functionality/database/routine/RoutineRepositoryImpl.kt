package com.patrykandpatryk.liftapp.functionality.database.routine

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseNames
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val routineWithExerciseNamesToDomainMapper: Mapper<RoutineWithExerciseNamesView, RoutineWithExerciseNames>,
    private val routineWithExercisesToDomainMapper: Mapper<RoutineWithExercisesRelation, RoutineWithExercises>,
    private val routineToDomainMapper: Mapper<RoutineEntity, Routine>,
    private val routineToEntityMapper: Mapper<Routine, RoutineEntity>,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : RoutineRepository {

    override fun getRoutinesWithNames(): Flow<List<RoutineWithExerciseNames>> =
        routineDao
            .getRoutinesWithExerciseNames()
            .map(routineWithExerciseNamesToDomainMapper::invoke)
            .flowOn(dispatcher)

    override fun getRoutineWithExercises(routineId: Long): Flow<RoutineWithExercises?> =
        routineDao
            .getRoutineWithExercises(routineId = routineId)
            .map(routineWithExercisesToDomainMapper::mapNullable)

    override fun getRoutine(id: Long): Flow<Routine?> =
        routineDao
            .getRoutine(id)
            .map(routineToDomainMapper::mapNullable)

    override suspend fun upsert(routine: Routine): Long = withContext(NonCancellable) {
        routineDao.upsert(routine = routineToEntityMapper(routine))
    }

    override suspend fun insert(name: String, exerciseIds: List<Long>): Long {
        val routineId = routineDao.insert(
            routine = RoutineEntity(
                name = name,
            ),
        )

        exerciseIds.map { exerciseId ->
            ExerciseWithRoutineEntity(
                routineId = routineId,
                exerciseId = exerciseId,
            )
        }.also { exerciseWithRoutineEntities -> routineDao.insert(exerciseWithRoutineEntities) }

        return routineId
    }

    override suspend fun delete(routineId: Long) {
        routineDao.delete(routineId)
    }
}
