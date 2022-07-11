package com.patrykandpatryk.liftapp.core.viewmodel

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.autoSaver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable

@OptIn(SavedStateHandleSaveableApi::class)
interface SavedStateHandleViewModel {

    val savedStateHandle: SavedStateHandle

    fun <T : Any> saveable(
        key: String,
        saver: Saver<T, out Any> = autoSaver(),
        init: () -> T,
    ) = savedStateHandle.saveable(
        key = key,
        saver = saver,
        init = init,
    )
}
