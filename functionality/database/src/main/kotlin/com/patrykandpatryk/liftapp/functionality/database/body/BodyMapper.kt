package com.patrykandpatryk.liftapp.functionality.database.body

import com.patrykandpatryk.liftapp.domain.body.Body
import com.patrykandpatryk.liftapp.domain.body.BodyEntry
import com.patrykandpatryk.liftapp.domain.body.BodyItem
import com.patrykandpatryk.liftapp.domain.body.BodyWithLatestEntry
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import javax.inject.Inject

class BodyMapper @Inject constructor(
    private val formatter: Formatter,
    private val stringProvider: StringProvider,
) {

    fun toDomain(body: BodyEntity): Body = Body(
        id = body.id,
        name = stringProvider.getResolvedName(body.name),
        type = body.type,
    )

    suspend fun toDomain(entry: BodyEntryEntity): BodyEntry = BodyEntry(
        id = entry.id,
        values = entry.values,
        formattedDate = formatter.getFormattedDate(entry.timestamp),
    )

    suspend fun toDomain(input: BodyWithLatestEntryView): BodyWithLatestEntry = BodyWithLatestEntry(
        id = input.body.id,
        name = stringProvider.getResolvedName(input.body.name),
        type = input.body.type,
        latestEntry = input.entry?.let { entry -> toDomain(entry) },
    )

    suspend fun toBodyItem(input: BodyWithLatestEntryView): BodyItem = BodyItem(
        id = input.body.id,
        type = input.body.type,
        title = stringProvider.getResolvedName(input.body.name),
        latestRecord = input
            .entry
            ?.let { entry ->
                BodyItem.LatestRecord(
                    formattedDate = formatter.getFormattedDate(entry.timestamp),
                    value = entry.values.toString(), // FIXME
                )
            },
    )
}
