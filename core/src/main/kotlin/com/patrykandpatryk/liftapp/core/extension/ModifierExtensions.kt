package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.ui.Modifier

inline fun Modifier.thenIf(
    condition: Boolean,
    other: Modifier.() -> Modifier,
) = if (condition) other() else this

inline fun <T> Modifier.thenIfNotNull(
    value: T?,
    other: Modifier.(T) -> Modifier,
) = if (value != null) other(value) else this
