package com.patrykandpatryk.liftapp.domain.validation

import java.io.Serializable

sealed class Validatable<T>(val value: T, val isValid: Boolean) : Serializable {

    val isInvalid: Boolean
        get() = isValid.not()

    class Valid<T>(value: T) : Validatable<T>(value, true)

    class Invalid<T>(
        value: T,
        val message: String? = null,
    ) : Validatable<T>(value, false)

    companion object {
        const val serialVersionUID = 1L
    }
}

fun <T> T.toValid() = Validatable.Valid(this)

fun <T> T.toInvalid(message: String? = null) = Validatable.Invalid(this, message)

fun <T> T.validate(validator: Validator<T>): Validatable<T> = validator.validate(this)

fun <T, R> Validatable<T>.map(transform: (T) -> R): Validatable<R> = when (this) {
    is Validatable.Invalid -> Validatable.Invalid(transform(value), message)
    is Validatable.Valid -> Validatable.Valid(transform(value))
}
