package com.patrykandpatryk.liftapp.domain.bodymeasurement

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.LocalDateTime

class UpsertBodyMeasurementUseCase @AssistedInject constructor(
    @Assisted private val id: Long,
    @Assisted private val entryId: Long?,
    private val repository: BodyMeasurementRepository,
) {
    suspend fun invoke(value: BodyMeasurementValue, time: LocalDateTime) {
        if (entryId != null) {
            repository.updateBodyMeasurementEntry(entryId, id, value, time)
        } else {
            repository.insertBodyMeasurementEntry(id, value, time)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long, entryId: Long?): UpsertBodyMeasurementUseCase
    }
}
