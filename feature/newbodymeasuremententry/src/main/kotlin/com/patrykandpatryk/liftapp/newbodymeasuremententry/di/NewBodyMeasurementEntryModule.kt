package com.patrykandpatryk.liftapp.newbodymeasuremententry.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.navigation.data.NewBodyMeasurementRouteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface NewBodyMeasurementEntryModule {
    companion object {
        @Provides
        fun provideNewBodyMeasurementRouteData(
            savedStateHandle: SavedStateHandle
        ): NewBodyMeasurementRouteData = savedStateHandle.toRoute()
    }
}
