package com.patrykandpatryk.liftapp.functionality.database.measurement

import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementEntry
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementWithLatestEntry
import javax.inject.Inject

class MeasurementWithLatestEntryToDomainMeasurementMapper @Inject constructor(
    private val entryToDomainMapper: Mapper<MeasurementWithLatestEntryView, MeasurementEntry?>,
) : Mapper<MeasurementWithLatestEntryView, MeasurementWithLatestEntry> {

    override fun map(input: MeasurementWithLatestEntryView): MeasurementWithLatestEntry =
        MeasurementWithLatestEntry(
            id = input.measurement.id,
            name = input.measurement.name,
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
