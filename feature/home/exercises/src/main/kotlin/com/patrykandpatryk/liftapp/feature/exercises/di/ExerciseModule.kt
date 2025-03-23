package com.patrykandpatryk.liftapp.feature.exercises.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ExerciseModule {
    companion object {
        @Provides
        fun provideExerciseListRouteData(
            savedStateHandle: SavedStateHandle
        ): ExerciseListRouteData = savedStateHandle.toRoute(typeMap = ExerciseListRouteData.typeMap)
    }
}
