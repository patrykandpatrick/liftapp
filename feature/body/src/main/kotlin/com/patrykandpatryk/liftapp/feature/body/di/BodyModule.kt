package com.patrykandpatryk.liftapp.feature.body.di

import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.feature.body.ui.BodyItem
import com.patrykandpatryk.liftapp.feature.body.ui.BodyItemMapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface BodyModule {

    @Binds
    fun bindBodyItemMapper(mapper: BodyItemMapper): Mapper<MeasurementWithLatestEntry, BodyItem>
}
