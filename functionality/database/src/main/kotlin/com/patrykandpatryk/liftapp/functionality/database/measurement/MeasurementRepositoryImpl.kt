package com.patrykandpatryk.liftapp.functionality.database.measurement

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.measurement.Measurement
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementRepository
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementValues
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementWithLatestEntry
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

typealias DomainMapper = Mapper<MeasurementWithLatestEntryView, MeasurementWithLatestEntry>

class MeasurementRepositoryImpl @Inject constructor(
    private val dao: MeasurementDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val measurementWithLatestEntryToDomainMapper: DomainMapper,
) : MeasurementRepository {

    override fun getAllMeasurements(): Flow<List<MeasurementWithLatestEntry>> =
        dao
            .getMeasurementsWithLatestEntries()
            .map(measurementWithLatestEntryToDomainMapper::invoke)

    override suspend fun insertMeasurement(
        measurement: Measurement.Insert,
    ) = withContext(dispatcher) {
        dao.insert(
            MeasurementEntity(
                name = measurement.name,
                type = measurement.type,
            ),
        )
    }

    override suspend fun insertMeasurements(measurements: List<Measurement.Insert>) {
        measurements.forEach { measurement -> insertMeasurement(measurement) }
    }

    override suspend fun insertMeasurementEntry(
        parentId: Long,
        values: MeasurementValues,
        timestamp: Date,
    ) = withContext(dispatcher) {
        dao.insert(
            MeasurementEntryEntity(
                parentId = parentId,
                values = values,
                timestamp = timestamp,
            ),
        )
    }
}
