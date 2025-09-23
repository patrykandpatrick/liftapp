package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.date.DateInterval
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

interface GetBodyMeasurementEntriesUseCase {
    fun getBodyMeasurementEntries(
        bodyMeasurementID: Long,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ): Flow<List<BodyMeasurementEntry>>
}

operator fun GetBodyMeasurementEntriesUseCase.invoke(
    bodyMeasurementID: Long,
    dateInterval: DateInterval,
): Flow<List<BodyMeasurementEntry>> =
    getBodyMeasurementEntries(
        bodyMeasurementID,
        dateInterval.periodStartTime,
        dateInterval.periodEndTime,
    )
