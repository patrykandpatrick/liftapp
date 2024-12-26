package com.patrykandpatryk.liftapp.domain.type

fun <T : Enum<T>> T.isAnyOf(vararg values: T): Boolean = values.contains(this)
