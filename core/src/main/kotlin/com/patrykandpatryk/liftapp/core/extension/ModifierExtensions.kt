package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.ui.Modifier

inline fun Modifier.thenIf(
    condition: Boolean,
    crossinline other: Modifier.() -> Modifier,
) = if (condition) other() else this

inline fun <T> Modifier.thenIfNotNull(
    value: T?,
    crossinline other: Modifier.(T) -> Modifier,
) = if (value != null) other(value) else this
