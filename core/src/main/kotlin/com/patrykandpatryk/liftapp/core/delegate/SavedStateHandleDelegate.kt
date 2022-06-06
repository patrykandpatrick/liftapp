package com.patrykandpatryk.liftapp.core.delegate

import com.patrykandpatryk.liftapp.core.viewmodel.SavedStateHandleViewModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SavedStateHandleDelegate<T>(
    private val key: String,
    private val defaultValue: T,
) : ReadWriteProperty<SavedStateHandleViewModel, T> {

    override fun getValue(thisRef: SavedStateHandleViewModel, property: KProperty<*>) =
        thisRef.savedStateHandle.get<T>(key = key) ?: defaultValue

    override fun setValue(thisRef: SavedStateHandleViewModel, property: KProperty<*>, value: T) {
        thisRef.savedStateHandle[key] = value
    }
}
