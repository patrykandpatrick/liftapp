package com.patrykandpatryk.liftapp.feature.routine.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.navigation.Routes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RoutineModule {

    companion object {

        @RoutineId
        @Provides
        fun provideRoutineId(savedStateHandle: SavedStateHandle): Long =
            requireNotNull(savedStateHandle[Routes.ARG_ID])
    }
}
