package com.patrykandpatryk.liftapp.functionality.database.bodymeasurement

import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurement
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.domain.date.millisToCalendar
import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BodyMeasurementRepositoryImpl @Inject constructor(
    private val dao: BodyMeasurementDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val bodyMeasurementMapper: BodyMeasurementMapper,
) : BodyMeasurementRepository {

    override fun getBodyMeasurement(id: Long): Flow<BodyMeasurement> =
        dao
            .getBodyMeasurement(id)
            .map(bodyMeasurementMapper::toDomain)
            .flowOn(dispatcher)

    override fun getBodyMeasurementWithLatestEntry(id: Long): Flow<BodyMeasurementWithLatestEntry> =
        dao
            .getBodyMeasurementWithLatestEntry(id)
            .map(bodyMeasurementMapper::toDomain)
            .flowOn(dispatcher)

    override fun getBodyMeasurementsWithLatestEntries(): Flow<List<BodyMeasurementWithLatestEntry>> =
        dao
            .getBodyMeasurementsWithLatestEntries()
            .map { entries -> entries.map { entry -> bodyMeasurementMapper.toDomain(entry) } }
            .flowOn(dispatcher)

    override fun getBodyMeasurementEntries(bodyMeasurementID: Long): Flow<List<BodyMeasurementEntry>> =
        dao
            .getBodyMeasurementEntries(bodyMeasurementID)
            .map { entries -> entries.map { entry -> bodyMeasurementMapper.toDomain(entry) } }
            .flowOn(dispatcher)

    override suspend fun getBodyMeasurementEntry(id: Long): BodyMeasurementEntry? = withContext(dispatcher) {
        dao
            .getBodyMeasurementEntry(id)
            ?.let { entry -> bodyMeasurementMapper.toDomain(entry) }
    }

    override suspend fun insertBodyMeasurement(
        bodyMeasurement: BodyMeasurement.Insert,
    ) = withContext(dispatcher) {
        dao.insertBodyMeasurement(
            BodyMeasurementEntity(
                name = bodyMeasurement.name,
                type = bodyMeasurement.type,
            ),
        )
    }

    override suspend fun insertBodyMeasurements(bodyMeasurements: List<BodyMeasurement.Insert>) {
        bodyMeasurements.forEach { bodyMeasurement -> insertBodyMeasurement(bodyMeasurement) }
    }

    override suspend fun insertBodyMeasurementEntry(
        bodyMeasurementID: Long,
        value: BodyMeasurementValue,
        timestamp: Long,
    ) = withContext(dispatcher) {
        dao.insertBodyMeasurementEntry(
            BodyMeasurementEntryEntity(
                bodyMeasurementID = bodyMeasurementID,
                value = value,
                timestamp = timestamp.millisToCalendar(),
            ),
        )
    }

    override suspend fun updateBodyMeasurementEntry(
        id: Long,
        bodyMeasurementID: Long,
        value: BodyMeasurementValue,
        timestamp: Long,
    ) = withContext(dispatcher) {
        dao.updateBodyMeasurementEntry(
            BodyMeasurementEntryEntity(
                id = id,
                bodyMeasurementID = bodyMeasurementID,
                value = value,
                timestamp = timestamp.millisToCalendar(),
            ),
        )
    }
}
