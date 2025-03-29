package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.LocalDateTime

class UpsertBodyMeasurementUseCase
@AssistedInject
constructor(
    @Assisted("id") private val id: Long,
    @Assisted("entryID") private val entryId: Long,
    private val repository: BodyMeasurementRepository,
) {
    suspend fun invoke(value: BodyMeasurementValue, time: LocalDateTime) {
        if (entryId != ID_NOT_SET) {
            repository.updateBodyMeasurementEntry(entryId, id, value, time)
        } else {
            repository.insertBodyMeasurementEntry(id, value, time)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Long,
            @Assisted("entryID") entryId: Long,
        ): UpsertBodyMeasurementUseCase
    }
}
