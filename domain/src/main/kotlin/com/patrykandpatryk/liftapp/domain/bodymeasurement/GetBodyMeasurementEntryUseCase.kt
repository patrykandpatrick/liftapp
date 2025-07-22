package com.patrykandpatryk.liftapp.domain.bodymeasurement

import kotlinx.coroutines.flow.Flow

fun interface GetBodyMeasurementEntryUseCase {
    fun getBodyMeasurementEntry(id: Long): Flow<BodyMeasurementEntry?>
}

operator fun GetBodyMeasurementEntryUseCase.invoke(id: Long) = getBodyMeasurementEntry(id)
