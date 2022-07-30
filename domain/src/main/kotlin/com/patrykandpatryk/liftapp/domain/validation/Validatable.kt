package com.patrykandpatryk.liftapp.domain.validation

import java.io.Serializable

sealed class Validatable<T>(val value: T, val isValid: Boolean) : Serializable {

    val isInvalid: Boolean
        get() = isValid.not()

    class Valid<T>(value: T) : Validatable<T>(value, true)

    class Invalid<T>(value: T) : Validatable<T>(value, false)

    companion object {
        const val serialVersionUID = 1L
    }
}

fun <T> T.toValid() = Validatable.Valid(this)

fun <T> T.toInvalid() = Validatable.Invalid(this)
