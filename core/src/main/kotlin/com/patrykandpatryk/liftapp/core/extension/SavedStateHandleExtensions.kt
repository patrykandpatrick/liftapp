package com.patrykandpatryk.liftapp.core.extension

import androidx.lifecycle.SavedStateHandle

inline fun <T> SavedStateHandle.update(key: String, block: (T?) -> T?): T? {
    val updated = block(get(key))
    set(key, updated)
    return updated
}
