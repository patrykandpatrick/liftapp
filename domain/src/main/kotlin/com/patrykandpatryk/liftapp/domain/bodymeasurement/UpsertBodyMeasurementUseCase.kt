package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import java.time.LocalDateTime

fun interface UpsertBodyMeasurementUseCase {
    suspend fun insertBodyMeasurementEntry(
        bodyMeasurementID: Long,
        value: BodyMeasurementValue,
        time: LocalDateTime,
        entryID: Long,
    )
}

suspend operator fun UpsertBodyMeasurementUseCase.invoke(
    bodyMeasurementID: Long,
    value: BodyMeasurementValue,
    time: LocalDateTime,
    entryID: Long = ID_NOT_SET,
) {
    insertBodyMeasurementEntry(
        bodyMeasurementID = bodyMeasurementID,
        value = value,
        time = time,
        entryID = entryID,
    )
}
