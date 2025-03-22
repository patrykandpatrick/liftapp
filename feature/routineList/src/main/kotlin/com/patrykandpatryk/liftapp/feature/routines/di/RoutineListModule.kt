package com.patrykandpatryk.liftapp.feature.routines.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.RoutineListRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RoutineListModule {
    companion object {
        @Provides
        fun provideRoutineListRouteData(savedStateHandle: SavedStateHandle): RoutineListRouteData =
            savedStateHandle.toRoute()
    }
}
