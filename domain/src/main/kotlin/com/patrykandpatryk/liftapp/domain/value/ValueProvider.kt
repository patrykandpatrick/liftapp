package com.patrykandpatryk.liftapp.domain.value

interface ValueProvider<out T> {
    val value: T
}
