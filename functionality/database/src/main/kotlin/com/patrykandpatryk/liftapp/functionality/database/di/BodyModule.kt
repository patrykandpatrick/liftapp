package com.patrykandpatryk.liftapp.functionality.database.di

import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementEntryUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementWithLatestEntryUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.UpsertBodyMeasurementUseCase
import com.patrykandpatryk.liftapp.functionality.database.bodymeasurement.BodyMeasurementRepositoryImpl
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
}
