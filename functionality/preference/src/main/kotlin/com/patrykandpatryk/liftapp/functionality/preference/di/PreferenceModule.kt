package com.patrykandpatryk.liftapp.functionality.preference.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.functionality.preference.repository.PreferenceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val PREFERENCES_NAME = "preferences"

@Module
@InstallIn(SingletonComponent::class)
interface PreferenceModule {

    @Binds
    fun bindPreferenceRepository(repositoryImpl: PreferenceRepositoryImpl): PreferenceRepository

    companion object {

        @Provides
        @Singleton
        fun provideDataStore(application: Application): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                produceFile = { application.preferencesDataStoreFile(PREFERENCES_NAME) },
            )
    }
}
