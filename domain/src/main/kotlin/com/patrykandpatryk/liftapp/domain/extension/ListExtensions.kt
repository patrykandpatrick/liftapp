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

fun <T> MutableList<T>.getOrPut(index: Int, put: () -> T): T =
    if (index < size) get(index)
    else {
        val newValue = put()
        add(newValue)
        newValue
    }

fun <T> MutableList<T>.addOrSet(index: Int, element: T) {
    if (index >= size) {
        add(element)
    } else {
        set(index, element)
    }
}
