package com.patrykandpatryk.liftapp.functionality.database.di

import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseNames
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineEntity
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineToDomainMapper
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineToEntityMapper
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineWithExerciseNamesToDomainMapper
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineWithExerciseNamesView
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineWithExercisesRelation
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineWithExercisesToDomainMapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RoutineMappersModule {

    @Binds
    fun bindRoutineWithExerciseNamesMapper(
        mapper: RoutineWithExerciseNamesToDomainMapper,
    ): Mapper<RoutineWithExerciseNamesView, RoutineWithExerciseNames>

    @Binds
    fun bindRoutineWithExercisesToDomainMapper(
        mapper: RoutineWithExercisesToDomainMapper,
    ): Mapper<RoutineWithExercisesRelation, RoutineWithExercises>

    @Binds
    fun bindRoutineToDomainMapper(
        mapper: RoutineToDomainMapper,
    ): Mapper<RoutineEntity, Routine>

    @Binds
    fun bindRoutineToEntityMapper(
        mapper: RoutineToEntityMapper,
    ): Mapper<Routine, RoutineEntity>
}
