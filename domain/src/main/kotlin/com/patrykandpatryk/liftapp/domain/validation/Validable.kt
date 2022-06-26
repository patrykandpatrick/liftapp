package com.patrykandpatryk.liftapp.domain.validation

sealed class Validable<T>(val value: T, val isValid: Boolean) {

    val isInvalid: Boolean
        get() = isValid.not()

    class Valid<T>(value: T) : Validable<T>(value, true)

    class Invalid<T>(value: T) : Validable<T>(value, false)
}

fun <T> T.toValid() = Validable.Valid(this)

fun <T> T.toInValid() = Validable.Invalid(this)
