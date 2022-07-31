package com.patrykandpatryk.liftapp.domain.validation

fun interface Validator<T> {

    fun validate(value: T): Validatable<T>
}
