package com.patrykandpatrick.liftapp.domain.extension

fun <T> Collection<T>.toggle(item: T): List<T> =
    if (contains(item)) {
        minus(item)
    } else {
        plus(item)
    }

operator fun <T> List<T>.set(index: Int, value: T): List<T> = mapIndexed { i, t ->
    if (i == index) value else t
}

/**
 * Returns a copy of this list with the element at [fromIndex] moved to [toIndex], or this list if
 * either index is out of bounds.
 */
fun <T> List<T>.moved(fromIndex: Int, toIndex: Int): List<T> =
    if (fromIndex in indices && toIndex in indices) {
        toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
    } else {
        this
    }

fun <T> MutableList<T>.getOrPut(index: Int, put: () -> T): T =
    if (index < size) {
        get(index)
    } else {
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
