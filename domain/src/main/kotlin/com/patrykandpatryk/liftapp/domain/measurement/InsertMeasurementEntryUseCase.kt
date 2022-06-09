package com.patrykandpatryk.liftapp.domain.measurement

import java.util.Date
import javax.inject.Inject

class InsertMeasurementEntryUseCase @Inject constructor(
    private val repository: MeasurementRepository,
) {

    suspend operator fun invoke(
        parentId: Long,
        values: MeasurementValues,
        timestamp: Date,
    ) {
        repository.insertMeasurementEntry(
            parentId = parentId,
            values = values,
            timestamp = timestamp,
        )
    }
}
