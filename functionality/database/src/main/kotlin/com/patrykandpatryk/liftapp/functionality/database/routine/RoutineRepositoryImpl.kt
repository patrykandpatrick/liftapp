package com.patrykandpatryk.liftapp.functionality.database.routine

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseNames
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val routineWithExerciseNamesToDomainMapper: Mapper<RoutineWithExerciseNamesView, RoutineWithExerciseNames>,
    private val routineWithExercisesToDomainMapper: Mapper<RoutineWithExercisesRelation, RoutineWithExercises>,
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
}
