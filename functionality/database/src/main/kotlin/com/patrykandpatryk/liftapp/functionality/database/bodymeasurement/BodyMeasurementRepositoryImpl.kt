package com.patrykandpatryk.liftapp.functionality.database.bodymeasurement

import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurement
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementEntryUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementWithLatestEntryUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.UpsertBodyMeasurementUseCase
import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BodyMeasurementRepositoryImpl
@Inject
constructor(
    private val dao: BodyMeasurementDao,
    @param:IODispatcher private val dispatcher: CoroutineDispatcher,
    private val bodyMeasurementMapper: BodyMeasurementMapper,
) :
    BodyMeasurementRepository,
    UpsertBodyMeasurementUseCase,
    GetBodyMeasurementEntryUseCase,
    GetBodyMeasurementWithLatestEntryUseCase {

    override fun getBodyMeasurement(id: Long): Flow<BodyMeasurement> =
        dao.getBodyMeasurement(id).map(bodyMeasurementMapper::toDomain).flowOn(dispatcher)

    override fun getBodyMeasurementWithLatestEntry(id: Long): Flow<BodyMeasurementWithLatestEntry> =
        dao.getBodyMeasurementWithLatestEntry(id)
            .map(bodyMeasurementMapper::toDomain)
            .flowOn(dispatcher)

    override fun getBodyMeasurementsWithLatestEntries():
        Flow<List<BodyMeasurementWithLatestEntry>> =
        dao.getBodyMeasurementsWithLatestEntries()
            .map { entries -> entries.map { entry -> bodyMeasurementMapper.toDomain(entry) } }
            .flowOn(dispatcher)

    override fun getBodyMeasurementEntries(
        bodyMeasurementID: Long
    ): Flow<List<BodyMeasurementEntry>> =
        dao.getBodyMeasurementEntries(bodyMeasurementID)
            .map { entries -> entries.map { entry -> bodyMeasurementMapper.toDomain(entry) } }
            .flowOn(dispatcher)

    override fun getBodyMeasurementEntry(id: Long): Flow<BodyMeasurementEntry?> =
        dao.getBodyMeasurementEntry(id)
            .map { entry -> entry?.let { bodyMeasurementMapper.toDomain(it) } }
            .flowOn(dispatcher)

    override suspend fun insertBodyMeasurement(bodyMeasurement: BodyMeasurement.Insert) =
        withContext(dispatcher) {
            dao.insertBodyMeasurement(
                BodyMeasurementEntity(name = bodyMeasurement.name, type = bodyMeasurement.type)
            )
        }

    override suspend fun insertBodyMeasurements(bodyMeasurements: List<BodyMeasurement.Insert>) {
        bodyMeasurements.forEach { bodyMeasurement -> insertBodyMeasurement(bodyMeasurement) }
    }

    override suspend fun insertBodyMeasurementEntry(
        bodyMeasurementID: Long,
        value: BodyMeasurementValue,
        time: LocalDateTime,
        entryID: Long,
    ) =
        withContext(dispatcher) {
            dao.insertBodyMeasurementEntry(
                BodyMeasurementEntryEntity(
                    id = entryID,
                    bodyMeasurementID = bodyMeasurementID,
                    value = value,
                    time = time,
                )
            )
        }

    override suspend fun deleteBodyMeasurementEntry(id: Long) {
        withContext(dispatcher) { dao.deleteBodyMeasurementEntry(id) }
    }
}
