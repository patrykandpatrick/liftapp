package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.navigation.Routes.ARG_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface BodyMeasurementDetailModule {

    companion object {

        @BodyMeasurementID
        @Provides
        fun provideBodyMeasurementID(savedStateHandle: SavedStateHandle): Long =
            requireNotNull(savedStateHandle[ARG_ID])
    }
}
