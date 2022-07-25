package com.patrykandpatryk.liftapp.feature.body.ui

import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementRepository
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementWithLatestEntry
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetBodyItemsUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val mapper: Mapper<MeasurementWithLatestEntry, BodyItem>,
) {

    operator fun invoke(): Flow<List<BodyItem>> =
        measurementRepository
            .getAllMeasurements()
            .map { mapper(it) }
}
