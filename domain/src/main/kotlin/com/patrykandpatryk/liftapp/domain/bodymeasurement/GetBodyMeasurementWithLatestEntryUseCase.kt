package com.patrykandpatryk.liftapp.domain.bodymeasurement

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

class GetBodyMeasurementWithLatestEntryUseCase
@AssistedInject
constructor(@Assisted private val id: Long, private val repository: BodyMeasurementRepository) {
    suspend fun invoke(): BodyMeasurementWithLatestEntry =
        repository.getBodyMeasurementWithLatestEntry(id).first()

    @AssistedFactory
    interface Factory {
        fun create(id: Long): GetBodyMeasurementWithLatestEntryUseCase
    }
}
