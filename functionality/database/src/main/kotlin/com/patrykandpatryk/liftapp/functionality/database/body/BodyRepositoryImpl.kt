package com.patrykandpatryk.liftapp.functionality.database.body

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.body.Body
import com.patrykandpatryk.liftapp.domain.body.BodyRepository
import com.patrykandpatryk.liftapp.domain.body.BodyValues
import com.patrykandpatryk.liftapp.domain.body.BodyWithLatestEntry
import com.patrykandpatryk.liftapp.domain.date.millisToCalendar
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BodyRepositoryImpl @Inject constructor(
    private val dao: BodyDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val bodyWithLatestEntryMapper: Mapper<BodyWithLatestEntryView, BodyWithLatestEntry>,
    private val bodyEntityMapper: Mapper<BodyEntity, Body>,
) : BodyRepository {

    override fun getBody(id: Long): Flow<Body> =
        dao
            .getBody(id)
            .map(bodyEntityMapper::invoke)

    override fun getAllBodies(): Flow<List<BodyWithLatestEntry>> =
        dao
            .getBodiesWithLatestEntries()
            .map(bodyWithLatestEntryMapper::invoke)

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
}
