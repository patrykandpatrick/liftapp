package com.patrykandpatryk.liftapp.feature.exercises.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.navigation.Routes.ARG_PICKING_MODE
import com.patrykandpatryk.liftapp.core.navigation.Routes.DISABLED_EXERCISE_IDS
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.exercises.mapper.ExerciseToItemMapper
import com.patrykandpatryk.liftapp.feature.exercises.ui.ExercisesItem
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ExerciseModule {

    @Binds
    fun bindExerciseToItemMapper(mapper: ExerciseToItemMapper):
        Mapper<Pair<Exercise, String>, ExercisesItem.Exercise>

    companion object {

        @Provides
        @PickingMode
        fun providePickingMode(savedStateHandle: SavedStateHandle): Boolean =
            savedStateHandle[ARG_PICKING_MODE] ?: false

        @Provides
        @DisabledExercises
        fun provideDisabledExercises(savedStateHandle: SavedStateHandle): List<Long> =
            savedStateHandle[DISABLED_EXERCISE_IDS] ?: emptyList()
    }
}
