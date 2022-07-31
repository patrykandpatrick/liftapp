package com.patrykandpatryk.liftapp.functionality.database.measurement

import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.measurement.Measurement
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementEntry
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import javax.inject.Inject

class MeasurementEntityToDomainMapper @Inject constructor(
    private val nameResolver: NameResolver,
) : Mapper<MeasurementEntity, Measurement> {

    override fun map(input: MeasurementEntity): Measurement = Measurement(
        id = input.id,
        name = nameResolver.getResolvedString(input.name),
        type = input.type,
    )
}

class MeasurementWithLatestEntryToDomainMeasurementMapper @Inject constructor(
    private val entryToDomainMapper: Mapper<MeasurementWithLatestEntryView, MeasurementEntry?>,
    private val nameResolver: NameResolver,
) : Mapper<MeasurementWithLatestEntryView, MeasurementWithLatestEntry> {

    override fun map(input: MeasurementWithLatestEntryView): MeasurementWithLatestEntry =
        MeasurementWithLatestEntry(
            id = input.measurement.id,
            name = nameResolver.getResolvedString(input.measurement.name),
            type = input.measurement.type,
            latestEntry = entryToDomainMapper(input),
        )
}

class MeasurementEntryEntityToDomainMapper @Inject constructor() :
    Mapper<MeasurementWithLatestEntryView, MeasurementEntry?> {

    override fun map(input: MeasurementWithLatestEntryView): MeasurementEntry? =
        input.entry?.let { entry ->
            MeasurementEntry(
                values = entry.values,
                type = input.measurement.type,
                timestamp = entry.timestamp,
            )
        }
}
