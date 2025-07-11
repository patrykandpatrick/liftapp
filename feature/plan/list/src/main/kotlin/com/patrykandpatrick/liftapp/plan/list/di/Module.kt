package com.patrykandpatrick.liftapp.plan.list.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.PlanListRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface Module {
    companion object {
        @Provides
        fun providePlanListRouteData(savedStateHandle: SavedStateHandle): PlanListRouteData =
            savedStateHandle.toRoute()
    }
}
