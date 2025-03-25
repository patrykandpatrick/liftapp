package com.patrykandpatryk.liftapp.domain.model

sealed interface Loadable<out T : Any> {
    data class Success<T : Any>(val data: T) : Loadable<T>

    data class Error<T : Any>(val error: Throwable) : Loadable<T>

    data object Loading : Loadable<Nothing>
}

fun <T : Any> T.toLoadable(): Loadable<T> = Loadable.Success(this)
