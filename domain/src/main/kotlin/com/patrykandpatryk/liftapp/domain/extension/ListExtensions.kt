package com.patrykandpatryk.liftapp.domain.extension

fun <T> Collection<T>.toggle(item: T): List<T> =
    if (contains(item)) {
        minus(item)
    } else {
        plus(item)
    }
