package com.patrykandpatryk.liftapp.bodyentry.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.bodyentry.ui.Intent
import com.patrykandpatryk.liftapp.bodyentry.ui.ScreenState
import com.patrykandpatryk.liftapp.bodyentry.ui.BodyScreenStateHandler
import com.patrykandpatryk.liftapp.bodyentry.ui.Event
import com.patrykandpatryk.liftapp.core.navigation.Routes.ARG_ENTRY_ID
import com.patrykandpatryk.liftapp.core.navigation.Routes.ARG_ID
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface BodyEntryModule {

    @Binds
    fun bindStateHandler(handler: BodyScreenStateHandler): ScreenStateHandler<ScreenState, Intent, Event>

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
