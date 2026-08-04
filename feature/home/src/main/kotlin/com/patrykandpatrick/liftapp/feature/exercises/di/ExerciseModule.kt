package com.patrykandpatrick.liftapp.feature.exercises.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.Routes
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
        ): ExerciseListRouteData =
            if (savedStateHandle.contains(MODE_ARGUMENT)) {
                savedStateHandle.toRoute(typeMap = ExerciseListRouteData.typeMap)
            } else {
                Routes.Home.Exercises
            }

        private val MODE_ARGUMENT = ExerciseListRouteData::mode.name
    }
}
