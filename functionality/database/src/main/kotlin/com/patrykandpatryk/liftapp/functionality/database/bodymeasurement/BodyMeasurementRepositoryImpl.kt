package com.patrykandpatryk.liftapp.functionality.database.body

import com.patrykandpatryk.liftapp.domain.body.Body
import com.patrykandpatryk.liftapp.domain.body.BodyEntry
import com.patrykandpatryk.liftapp.domain.body.BodyItem
import com.patrykandpatryk.liftapp.domain.body.BodyRepository
import com.patrykandpatryk.liftapp.domain.body.BodyValues
import com.patrykandpatryk.liftapp.domain.body.BodyWithLatestEntry
import com.patrykandpatryk.liftapp.domain.date.millisToCalendar
import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BodyRepositoryImpl @Inject constructor(
    private val dao: BodyDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val bodyMapper: BodyMapper,
) : BodyRepository {

    override fun getBody(id: Long): Flow<Body> =
        dao
            .getBody(id)
            .map(bodyMapper::toDomain)
            .flowOn(dispatcher)

    override fun getBodyWithLatestEntry(id: Long): Flow<BodyWithLatestEntry> =
        dao
            .getBodyWithLatestEntry(id)
            .map(bodyMapper::toDomain)
            .flowOn(dispatcher)

    override fun getBodyItems(): Flow<List<BodyItem>> =
        dao
            .getBodiesWithLatestEntries()
            .map { entries -> entries.map { entry -> bodyMapper.toBodyItem(entry) } }
            .flowOn(dispatcher)

    override fun getEntries(bodyId: Long): Flow<List<BodyEntry>> =
        dao
            .getBodyEntries(bodyId)
            .map { entries -> entries.map { entry -> bodyMapper.toDomain(entry) } }
            .flowOn(dispatcher)

    override suspend fun getEntry(entryId: Long): BodyEntry? = withContext(dispatcher) {
        dao
            .getBodyEntry(entryId)
            ?.let { entry -> bodyMapper.toDomain(entry) }
    }

    override suspend fun insertBody(
        body: Body.Insert,
    ) = withContext(dispatcher) {
        dao.insert(
            BodyEntity(
                name = body.name,
                type = body.type,
            ),
        )
    }

    override suspend fun insertBodies(bodies: List<Body.Insert>) {
        bodies.forEach { body -> insertBody(body) }
    }

    override suspend fun insertBodyEntry(
        parentId: Long,
        values: BodyValues,
        timestamp: Long,
    ) = withContext(dispatcher) {
        dao.insert(
            BodyEntryEntity(
                parentId = parentId,
                values = values,
                timestamp = timestamp.millisToCalendar(),
            ),
        )
    }

    override suspend fun updateBodyEntry(
        entryId: Long,
        parentId: Long,
        values: BodyValues,
        timestamp: Long,
    ) = withContext(dispatcher) {
        dao.update(
            BodyEntryEntity(
                id = entryId,
                parentId = parentId,
                values = values,
                timestamp = timestamp.millisToCalendar(),
            ),
        )
    }
}
