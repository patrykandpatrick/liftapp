package com.patrykandpatrick.liftapp.newplan.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.NewPlanRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface NewPlanModule {
    companion object {
        @Provides
        fun provideNewPlanRouteData(savedStateHandle: SavedStateHandle): NewPlanRouteData =
            savedStateHandle.toRoute()
    }
}
