package com.patrykandpatryk.liftapp.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
interface CoroutinesModule {

    companion object {

        @Provides
        @MainDispatcher
        fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

        @Provides
        @MainDispatcher.Immediate
        fun provideMainImmediateDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate

        @Provides
        @DefaultDispatcher
        fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

        @Provides @IODispatcher fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}
