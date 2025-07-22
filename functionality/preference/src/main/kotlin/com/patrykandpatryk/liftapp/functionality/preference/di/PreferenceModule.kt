package com.patrykandpatryk.liftapp.functionality.preference.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.patrykandpatrick.opto.domain.Preference
import com.patrykandpatryk.liftapp.domain.di.PreferenceQualifier
import com.patrykandpatryk.liftapp.domain.plan.ActivePlan
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.functionality.preference.repository.PreferenceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

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
                produceFile = { application.preferencesDataStoreFile(PREFERENCES_NAME) }
            )

        @Provides
        @PreferenceQualifier.GoalInfoVisible
        fun provideGoalInfoVisiblePreference(
            repository: PreferenceRepository
        ): Preference<Boolean> = repository.goalInfoVisible

        @Provides
        @PreferenceQualifier.LongDistanceUnit
        fun provideLongDistanceUnitPreference(
            repository: PreferenceRepository
        ): Preference<LongDistanceUnit> = repository.longDistanceUnit

        @Provides
        @PreferenceQualifier.ActivePlan
        fun provideActivePlanIDPreference(
            repository: PreferenceRepository
        ): Preference<ActivePlan?> = repository.activePlan

        @Provides
        @PreferenceQualifier.Is24H
        fun provideIs24HPreference(repository: PreferenceRepository): Flow<Boolean> =
            repository.is24H
    }
}
