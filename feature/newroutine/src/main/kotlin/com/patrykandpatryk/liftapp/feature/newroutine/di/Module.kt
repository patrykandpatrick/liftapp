package com.patrykandpatryk.liftapp.feature.newroutine.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.NewRoutineRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface Module {
    companion object {
        @Provides
        fun provideNewRoutineRouteData(savedStateHandle: SavedStateHandle): NewRoutineRouteData =
            savedStateHandle.toRoute()
    }
}
