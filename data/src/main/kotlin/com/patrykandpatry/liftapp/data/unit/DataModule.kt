package com.patrykandpatry.liftapp.data.unit

import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds fun bindUnitConverter(converter: UnitConverterImpl): UnitConverter
}
