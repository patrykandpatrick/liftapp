package com.patrykandpatryk.liftapp.domain.bodymeasurement

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class GetBodyMeasurementEntryUseCase
@AssistedInject
constructor(@Assisted private val id: Long, private val repository: BodyMeasurementRepository) {
    suspend fun invoke(): BodyMeasurementEntry? = repository.getBodyMeasurementEntry(id)

    @AssistedFactory
    interface Factory {
        fun create(id: Long): GetBodyMeasurementEntryUseCase
    }
}
