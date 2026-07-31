package com.patrykandpatrick.liftapp.functionality.database.di

import com.patrykandpatrick.liftapp.domain.bodymeasurement.GetBodyMeasurementEntriesUseCase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.GetBodyMeasurementEntryUseCase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.GetBodyMeasurementWithLatestEntryUseCase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.GetBodyMeasurementsWithLatestEntriesUseCase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.UpsertBodyMeasurementUseCase
import com.patrykandpatrick.liftapp.functionality.database.bodymeasurement.BodyMeasurementRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BodyModule {

    @Binds
    fun bindUpsertBodyMeasurementUseCase(
        repository: BodyMeasurementRepositoryImpl
    ): UpsertBodyMeasurementUseCase

    @Binds
    fun bindGetBodyMeasurementEntryUseCase(
        repository: BodyMeasurementRepositoryImpl
    ): GetBodyMeasurementEntryUseCase

    @Binds
    fun bindGetBodyMeasurementWithLatestEntryUseCase(
        repository: BodyMeasurementRepositoryImpl
    ): GetBodyMeasurementWithLatestEntryUseCase

    @Binds
    fun bindGetBodyMeasurementsWithLatestEntriesUseCase(
        repository: BodyMeasurementRepositoryImpl
    ): GetBodyMeasurementsWithLatestEntriesUseCase

    @Binds
    fun bindGetBodyMeasurementEntriesUseCase(
        repository: BodyMeasurementRepositoryImpl
    ): GetBodyMeasurementEntriesUseCase
}
