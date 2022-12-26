package com.patrykandpatryk.liftapp.feature.routine.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.routine.mapper.ExerciseToItemMapper
import com.patrykandpatryk.liftapp.feature.routine.model.ExerciseItem
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RoutineModule {

    @Binds
    fun bindExerciseToItemMapper(mapper: ExerciseToItemMapper): Mapper<Exercise, ExerciseItem>

    companion object {

        @RoutineId
        @Provides
        fun provideRoutineId(savedStateHandle: SavedStateHandle): Long =
            requireNotNull(savedStateHandle[Routes.ARG_ID])
    }
}
