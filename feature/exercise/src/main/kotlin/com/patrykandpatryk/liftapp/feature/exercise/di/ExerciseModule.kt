package com.patrykandpatryk.liftapp.feature.exercise.di

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.exercise.mapper.ExerciseToItemMapper
import com.patrykandpatryk.liftapp.feature.exercise.ui.ExercisesItem
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ExerciseModule {

    @Binds
    fun bindExerciseToItemMapper(mapper: ExerciseToItemMapper): Mapper<Exercise, ExercisesItem.Exercise>
}
