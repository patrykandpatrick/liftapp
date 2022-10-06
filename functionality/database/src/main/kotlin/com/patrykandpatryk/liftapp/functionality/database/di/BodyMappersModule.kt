package com.patrykandpatryk.liftapp.functionality.database.di

import com.patrykandpatryk.liftapp.domain.body.Body
import com.patrykandpatryk.liftapp.domain.body.BodyEntry
import com.patrykandpatryk.liftapp.domain.body.BodyWithLatestEntry
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.functionality.database.body.BodyEntity
import com.patrykandpatryk.liftapp.functionality.database.body.BodyEntityToDomainMapper
import com.patrykandpatryk.liftapp.functionality.database.body.BodyEntryEntity
import com.patrykandpatryk.liftapp.functionality.database.body.BodyEntryEntityToDomainMapper
import com.patrykandpatryk.liftapp.functionality.database.body.BodyWithLatestEntryToDomainBodyMapper
import com.patrykandpatryk.liftapp.functionality.database.body.BodyWithLatestEntryView
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BodyMappersModule {

    @Binds
    fun bindBodyEntityToDomainMapper(
        mapper: BodyEntityToDomainMapper,
    ): Mapper<BodyEntity, Body>

    @Binds
    fun bindBodyWithLatestEntryToDomainMapper(
        mapper: BodyWithLatestEntryToDomainBodyMapper,
    ): Mapper<BodyWithLatestEntryView, BodyWithLatestEntry>

    @Binds
    fun bindBodyWithLatestEntryViewToDomainMapper(
        mapper: BodyEntryEntityToDomainMapper,
    ): Mapper<BodyEntryEntity, BodyEntry>
}
