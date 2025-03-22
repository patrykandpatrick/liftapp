package com.patrykandpatrick.liftapp.feature.workout.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.WorkoutRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface Module {
    companion object {
        @Provides
        fun provideWorkoutRoute(savedStateHandle: SavedStateHandle): WorkoutRouteData =
            savedStateHandle.toRoute()
    }
}
