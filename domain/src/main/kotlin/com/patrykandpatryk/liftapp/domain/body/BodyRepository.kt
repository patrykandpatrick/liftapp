package com.patrykandpatryk.liftapp.domain.body

import kotlinx.coroutines.flow.Flow

interface BodyRepository {

    fun getBody(id: Long): Flow<Body>

    fun getAllBodies(): Flow<List<BodyWithLatestEntry>>

    suspend fun insertBody(body: Body.Insert)

    suspend fun insertBodies(bodies: List<Body.Insert>)

    suspend fun insertBodyEntry(
        parentId: Long,
        values: BodyValues,
        timestamp: Long,
    )
}
