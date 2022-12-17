package com.patrykandpatryk.liftapp.bodyentry.di

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
internal interface BodyEntryModule {

    companion object {

        @BodyId
        @Provides
        fun provideBodyId(savedStateHandle: SavedStateHandle): Long =
            requireNotNull(savedStateHandle[ARG_ID])

        @BodyEntryId
        @Provides
        fun provideBodyEntryId(savedStateHandle: SavedStateHandle): Long =
            savedStateHandle[ARG_ENTRY_ID] ?: ID_NOT_SET
    }
}
