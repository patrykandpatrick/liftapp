package com.patrykandpatryk.liftapp.functionality.database.body

import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.body.Body
import com.patrykandpatryk.liftapp.domain.body.BodyEntry
import com.patrykandpatryk.liftapp.domain.body.BodyWithLatestEntry
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import javax.inject.Inject

class BodyEntityToDomainMapper @Inject constructor(
    private val nameResolver: NameResolver,
) : Mapper<BodyEntity, Body> {

    override fun map(input: BodyEntity): Body = Body(
        id = input.id,
        name = nameResolver.getResolvedString(input.name),
        type = input.type,
    )
}

class BodyWithLatestEntryToDomainBodyMapper @Inject constructor(
    private val entryToDomainMapper: Mapper<BodyWithLatestEntryView, BodyEntry?>,
    private val nameResolver: NameResolver,
) : Mapper<BodyWithLatestEntryView, BodyWithLatestEntry> {

    override fun map(input: BodyWithLatestEntryView): BodyWithLatestEntry =
        BodyWithLatestEntry(
            id = input.body.id,
            name = nameResolver.getResolvedString(input.body.name),
            type = input.body.type,
            latestEntry = entryToDomainMapper(input),
        )
}

class BodyEntryEntityToDomainMapper @Inject constructor() :
    Mapper<BodyWithLatestEntryView, BodyEntry?> {

    override fun map(input: BodyWithLatestEntryView): BodyEntry? =
        input.entry?.let { entry ->
            BodyEntry(
                values = entry.values,
                type = input.body.type,
                timestamp = entry.timestamp,
            )
        }
}
