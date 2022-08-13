package com.patrykandpatryk.liftapp.bodyrecord.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.bodyrecord.ui.Intent
import com.patrykandpatryk.liftapp.bodyrecord.ui.ScreenState
import com.patrykandpatryk.liftapp.bodyrecord.ui.BodyScreenStateHandler
import com.patrykandpatryk.liftapp.core.navigation.Routes.ARG_ID
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface BodyRecordModule {

    @Binds
    fun bindStateHandler(handler: BodyScreenStateHandler): ScreenStateHandler<ScreenState, Intent>

    companion object {

        @BodyId
        @Provides
        fun provideBodyId(savedStateHandle: SavedStateHandle): Long =
            requireNotNull(savedStateHandle[ARG_ID])
    }
}
