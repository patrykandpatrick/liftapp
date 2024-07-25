package com.patrykandpatryk.liftapp.domain.validation

import java.io.Serializable

sealed class Validatable<T>(val isValid: Boolean) : Serializable {

    abstract val value: T
    val isInvalid: Boolean
        get() = isValid.not()

    val errorMessage: String?
        get() = when (this) {
            is Invalid -> message
            is Valid -> null
        }

    data class Valid<T>(override val value: T) : Validatable<T>(true)

    data class Invalid<T>(
        override val value: T,
        val message: String? = null,
    ) : Validatable<T>(false)

    companion object {
        private const val serialVersionUID = 1L
    }
}

fun <T> T.toValid() = Validatable.Valid(this)

fun <T> T.toInvalid(message: String? = null) = Validatable.Invalid(this, message)

fun <T, R> Validatable<T>.map(transform: (T) -> R): Validatable<R> = when (this) {
    is Validatable.Invalid -> Validatable.Invalid(transform(value), message)
    is Validatable.Valid -> Validatable.Valid(transform(value))
}
