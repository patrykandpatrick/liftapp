package com.patrykandpatryk.liftapp.domain.extension

fun <T> Collection<T>.toggle(item: T): List<T> =
    if (contains(item)) {
        minus(item)
    } else {
        plus(item)
    }

operator fun <T> List<T>.set(index: Int, value: T): List<T> =
    mapIndexed { i, t ->
        if (i == index) value else t
    }
