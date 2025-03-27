package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.BodyMeasurementDetailsRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface BodyMeasurementDetailModule {
    companion object {
        @Provides
        fun provideBodyMeasurementDetailsRouteData(
            savedStateHandle: SavedStateHandle
        ): BodyMeasurementDetailsRouteData = savedStateHandle.toRoute()
    }
}
