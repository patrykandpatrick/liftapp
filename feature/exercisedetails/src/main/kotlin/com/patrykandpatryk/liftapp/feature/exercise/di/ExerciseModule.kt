package com.patrykandpatryk.liftapp.feature.exercise.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.ExerciseDetailsRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ExerciseModule {
    companion object {
        @Provides
        fun provideExerciseDetailsRouteData(
            savedStateHandle: SavedStateHandle
        ): ExerciseDetailsRouteData = savedStateHandle.toRoute()
    }
}
