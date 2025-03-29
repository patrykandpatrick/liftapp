package com.patrykandpatryk.liftapp.feature.routine.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.RoutineDetailsRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RoutineModule {
    companion object {
        @Provides
        fun provideRoutineDetailsRouteData(
            savedStateHandle: SavedStateHandle
        ): RoutineDetailsRouteData = savedStateHandle.toRoute()
    }
}
