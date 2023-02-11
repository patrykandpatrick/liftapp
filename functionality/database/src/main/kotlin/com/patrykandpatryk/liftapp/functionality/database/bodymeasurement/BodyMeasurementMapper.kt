package com.patrykandpatryk.liftapp.functionality.database.bodymeasurement

import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurement
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import javax.inject.Inject

class BodyMeasurementMapper @Inject constructor(
    private val formatter: Formatter,
    private val stringProvider: StringProvider,
) {

    fun toDomain(bodyMeasurement: BodyMeasurementEntity): BodyMeasurement = BodyMeasurement(
        id = bodyMeasurement.id,
        name = stringProvider.getResolvedName(bodyMeasurement.name),
        type = bodyMeasurement.type,
    )

    suspend fun toDomain(entry: BodyMeasurementEntryEntity) = BodyMeasurementEntry(
        id = entry.id,
        value = entry.value,
        formattedDate = formatter.getFormattedDate(entry.timestamp),
    )

    suspend fun toDomain(input: BodyMeasurementWithLatestEntryViewResult): BodyMeasurementWithLatestEntry =
        BodyMeasurementWithLatestEntry(
            id = input.bodyMeasurement.id,
            name = stringProvider.getResolvedName(input.bodyMeasurement.name),
            type = input.bodyMeasurement.type,
            latestEntry = input.entry?.let { entry -> toDomain(entry) },
        )
}
