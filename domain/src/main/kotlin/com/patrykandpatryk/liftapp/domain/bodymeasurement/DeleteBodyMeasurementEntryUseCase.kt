package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class DeleteBodyMeasurementEntryUseCase
@Inject
constructor(
    private val bodyMeasurementRepository: BodyMeasurementRepository,
    @IODispatcher private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Long) {
        withContext(coroutineDispatcher + NonCancellable) {
            bodyMeasurementRepository.deleteBodyMeasurementEntry(id)
        }
    }
}
