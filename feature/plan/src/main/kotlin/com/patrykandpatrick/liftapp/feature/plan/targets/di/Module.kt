package com.patrykandpatrick.liftapp.feature.plan.targets.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.PlanTargetsRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface Module {
    companion object {
        @Provides
        fun provideRouteData(savedStateHandle: SavedStateHandle): PlanTargetsRouteData =
            savedStateHandle.toRoute()
    }
}
