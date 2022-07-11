package com.patrykandpatryk.liftapp.domain.validation

import java.io.Serializable

sealed class Validable<T>(val value: T, val isValid: Boolean) : Serializable {

    val isInvalid: Boolean
        get() = isValid.not()

    class Valid<T>(value: T) : Validable<T>(value, true)

    class Invalid<T>(value: T) : Validable<T>(value, false)

    companion object {
        const val serialVersionUID = 1L
    }
}

fun <T> T.toValid() = Validable.Valid(this)

fun <T> T.toInValid() = Validable.Invalid(this)
