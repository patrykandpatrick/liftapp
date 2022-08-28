package com.patrykandpatryk.liftapp.functionality.database.body

import com.patrykandpatryk.liftapp.domain.body.Body
import com.patrykandpatryk.liftapp.domain.body.BodyEntry
import com.patrykandpatryk.liftapp.domain.body.BodyWithLatestEntry
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import javax.inject.Inject

class BodyEntityToDomainMapper @Inject constructor(
    private val nameResolver: NameResolver,
) : Mapper<BodyEntity, Body> {

    override suspend fun map(input: BodyEntity): Body = Body(
        id = input.id,
        name = nameResolver.getResolvedString(input.name),
        type = input.type,
    )
}

class BodyWithLatestEntryToDomainBodyMapper @Inject constructor(
    private val entryToDomainMapper: Mapper<BodyEntryEntity, BodyEntry>,
    private val nameResolver: NameResolver,
) : Mapper<BodyWithLatestEntryView, BodyWithLatestEntry> {

    override suspend fun map(input: BodyWithLatestEntryView): BodyWithLatestEntry = BodyWithLatestEntry(
        id = input.body.id,
        name = nameResolver.getResolvedString(input.body.name),
        type = input.body.type,
        latestEntry = input.entry?.let { entryToDomainMapper.invoke(it) },
    )
}

class BodyEntryEntityToDomainMapper @Inject constructor(
    private val formatter: Formatter,
) : Mapper<BodyEntryEntity, BodyEntry> {

    override suspend fun map(input: BodyEntryEntity): BodyEntry = BodyEntry(
        id = input.id,
        values = input.values,
        formattedDate = formatter.getFormattedDate(input.timestamp),
    )
}
