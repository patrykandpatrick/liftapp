package com.patrykandpatryk.liftapp.domain.body

import kotlinx.coroutines.flow.Flow

interface BodyRepository {

    fun getBody(id: Long): Flow<Body>

    fun getBodyWithLatestEntry(id: Long): Flow<BodyWithLatestEntry>

    fun getAllBodies(): Flow<List<BodyWithLatestEntry>>

    fun getEntries(bodyId: Long): Flow<List<BodyEntry>>

    suspend fun getEntry(entryId: Long): BodyEntry?

    suspend fun insertBody(body: Body.Insert)

    suspend fun insertBodies(bodies: List<Body.Insert>)

    suspend fun insertBodyEntry(
        parentId: Long,
        values: BodyValues,
        timestamp: Long,
    )

    suspend fun updateBodyEntry(
        entryId: Long,
        parentId: Long,
        values: BodyValues,
        timestamp: Long,
    )
}
