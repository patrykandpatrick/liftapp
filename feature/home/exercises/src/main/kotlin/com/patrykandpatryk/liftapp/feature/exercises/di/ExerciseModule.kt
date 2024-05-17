package com.patrykandpatryk.liftapp.feature.exercises.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.navigation.Routes.ARG_PICKING_MODE
import com.patrykandpatryk.liftapp.core.navigation.Routes.DISABLED_EXERCISE_IDS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ExerciseModule {

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
