package com.patrykandpatryk.liftapp.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {

    companion object {

        @Provides
        fun provideJson(): Json = Json
    }
}
