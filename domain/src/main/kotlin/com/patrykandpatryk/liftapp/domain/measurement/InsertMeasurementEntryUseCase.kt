package com.patrykandpatryk.liftapp.domain.measurement

import java.util.Calendar
import javax.inject.Inject

class InsertMeasurementEntryUseCase @Inject constructor(
    private val repository: MeasurementRepository,
) {

    suspend operator fun invoke(
        parentId: Long,
        values: MeasurementValues,
        timestamp: Calendar,
    ) {
        repository.insertMeasurementEntry(
            parentId = parentId,
            values = values,
            timestamp = timestamp,
        )
    }
}
