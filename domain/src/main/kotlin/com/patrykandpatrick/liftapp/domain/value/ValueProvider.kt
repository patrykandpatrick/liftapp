package com.patrykandpatrick.liftapp.domain.value

interface ValueProvider<out T> {
    val value: T
}
