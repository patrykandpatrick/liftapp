package com.patrykandpatryk.liftapp.newbodymeasuremententry.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.navigation.Routes.ARG_ENTRY_ID
import com.patrykandpatryk.liftapp.core.navigation.Routes.ARG_ID
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface NewBodyMeasurementEntryModule {

    companion object {

        @BodyMeasurementID
        @Provides
        fun provideBodyMeasurementID(savedStateHandle: SavedStateHandle): Long =
            requireNotNull(savedStateHandle[ARG_ID])

        @BodyMeasurementEntryID
        @Provides
        fun provideBodyMeasurementEntryID(savedStateHandle: SavedStateHandle): Long =
            savedStateHandle[ARG_ENTRY_ID] ?: ID_NOT_SET
    }
}
