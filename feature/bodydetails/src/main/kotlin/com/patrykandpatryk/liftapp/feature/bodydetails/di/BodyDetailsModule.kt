package com.patrykandpatryk.liftapp.feature.bodydetails.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.navigation.Routes.ARG_ID
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.feature.bodydetails.ui.BodyScreenStateHandler
import com.patrykandpatryk.liftapp.feature.bodydetails.ui.ScreenState
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface BodyDetailsModule {

    @Binds
    fun bindBodyDetailsHandler(handler: BodyScreenStateHandler): ScreenStateHandler<ScreenState, Unit, Unit>

    companion object {

        @BodyId
        @Provides
        fun provideBodyId(savedStateHandle: SavedStateHandle): Long =
            requireNotNull(savedStateHandle[ARG_ID])
    }
}
