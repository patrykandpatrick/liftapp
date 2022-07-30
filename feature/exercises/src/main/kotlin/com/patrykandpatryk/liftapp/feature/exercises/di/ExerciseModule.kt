package com.patrykandpatryk.liftapp.feature.exercises.di

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.exercises.mapper.ExerciseToItemMapper
import com.patrykandpatryk.liftapp.feature.exercises.ui.ExercisesItem
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ExerciseModule {

    @Binds
    fun bindExerciseToItemMapper(mapper: ExerciseToItemMapper):
        Mapper<Pair<Exercise, String>, ExercisesItem.Exercise>
}
