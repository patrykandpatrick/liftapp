package com.patrykandpatryk.liftapp.functionality.preference.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

interface PreferenceManager {
    val dataStore: DataStore<Preferences>

    fun <C, S> preference(
        key: Preferences.Key<S>,
        defaultValue: C,
        serialize: (C) -> S,
        deserialize: (S) -> C,
    ): PreferenceImpl<C, S> = PreferenceImpl(key, defaultValue, serialize, deserialize, dataStore)

    fun <S> preference(key: Preferences.Key<S>, defaultValue: S): PreferenceImpl<S, S> =
        preference(key = key, defaultValue = defaultValue, serialize = { it }, deserialize = { it })
}
