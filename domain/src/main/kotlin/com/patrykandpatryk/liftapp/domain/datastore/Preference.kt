package com.patrykandpatryk.liftapp.domain.datastore

import kotlinx.coroutines.flow.Flow

interface Preference<T> {
    fun get(): Flow<T>

    suspend fun set(value: T)

    suspend fun update(function: (T) -> T)
}
