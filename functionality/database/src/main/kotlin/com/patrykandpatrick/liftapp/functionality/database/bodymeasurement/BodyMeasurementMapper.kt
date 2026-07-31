package com.patrykandpatrick.liftapp.functionality.database.bodymeasurement

import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurement
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import javax.inject.Inject

class BodyMeasurementMapper
@Inject
constructor(private val formatter: Formatter, private val stringProvider: StringProvider) {

    fun toDomain(bodyMeasurement: BodyMeasurementEntity): BodyMeasurement =
        BodyMeasurement(
            id = bodyMeasurement.id,
            name = stringProvider.getResolvedName(bodyMeasurement.name),
            type = bodyMeasurement.type,
        )

    fun toDomain(entry: BodyMeasurementEntryEntity) =
        BodyMeasurementEntry(id = entry.id, value = entry.value, localDateTime = entry.time)

    fun toDomain(input: BodyMeasurementWithLatestEntryViewResult): BodyMeasurementWithLatestEntry =
        BodyMeasurementWithLatestEntry(
            id = input.bodyMeasurement.id,
            name = stringProvider.getResolvedName(input.bodyMeasurement.name),
            type = input.bodyMeasurement.type,
            latestEntry = input.entry?.let { entry -> toDomain(entry) },
        )
}
