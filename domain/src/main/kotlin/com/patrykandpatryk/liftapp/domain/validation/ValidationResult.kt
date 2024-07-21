package com.patrykandpatryk.liftapp.domain.validation

sealed class ValidationResult<T>(val valid: Boolean) {
    abstract val value: T

    data class Valid<T>(override val value: T) : ValidationResult<T>(true)
    data class Invalid<T>(
        override val value: T,
        val errorMessages: List<CharSequence>,
    ) : ValidationResult<T>(false)
}
