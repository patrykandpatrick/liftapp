package com.patrykandpatrick.liftapp.domain.extension

fun String.toDoubleOrZero(): Double = toDoubleOrNull() ?: 0.0

fun Any?.toStringOrEmpty(): String = this?.toString().orEmpty()
