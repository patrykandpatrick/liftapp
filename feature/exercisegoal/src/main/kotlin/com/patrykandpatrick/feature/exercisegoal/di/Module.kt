package com.patrykandpatrick.feature.exercisegoal.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.ExerciseGoalRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface Module {
    companion object {
        @Provides
        fun provideExerciseGoalRoute(savedStateHandle: SavedStateHandle): ExerciseGoalRouteData =
            savedStateHandle.toRoute()
    }
}
