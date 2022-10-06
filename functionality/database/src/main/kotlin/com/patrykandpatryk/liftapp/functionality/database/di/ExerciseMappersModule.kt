package com.patrykandpatryk.liftapp.functionality.database.di

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntityToDomainMapper
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseInsertToEntityMapper
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseUpdateToEntityMapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ExerciseMappersModule {

    @Binds
    fun bindExerciseEntityToDomainMapper(
        mapper: ExerciseEntityToDomainMapper,
    ): Mapper<ExerciseEntity, Exercise>

    @Binds
    fun bindExerciseInsertToEntityMapper(
        mapper: ExerciseInsertToEntityMapper,
    ): Mapper<Exercise.Insert, ExerciseEntity>

    @Binds
    fun bindExerciseUpdateToEntityMapper(
        mapper: ExerciseUpdateToEntityMapper,
    ): Mapper<Exercise.Update, ExerciseEntity.Update>
}
