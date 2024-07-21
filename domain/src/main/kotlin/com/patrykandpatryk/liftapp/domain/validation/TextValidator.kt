package com.patrykandpatryk.liftapp.domain.validation

import java.util.LinkedList

class TextValidator<T>(
    private val typeTextValidationElements: Set<TextValidationElement<T>>,
    private val stringTextValidationElements: Set<TextValidationElement<String>>,
) {
    fun validate(value: T, string: String): ValidationResult<T> {
        val errorMessages = LinkedList<CharSequence>()
        stringTextValidationElements.forEach { condition ->
            val result = condition.validate(string)
            if (result is TextValidationElement.Result.Invalid) {
                errorMessages.add(result.message)
            }
        }
        typeTextValidationElements.forEach { condition ->
            val result = condition.validate(value)
            if (result is TextValidationElement.Result.Invalid) {
                errorMessages.add(result.message)
            }
        }
        return if (errorMessages.isEmpty()) {
            ValidationResult.Valid(value)
        } else {
            ValidationResult.Invalid(value, errorMessages)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getTextValidatorElement(validatorClass: Class<T>): T? =
        typeTextValidationElements.find { it.javaClass == validatorClass } as? T
            ?: stringTextValidationElements.find { it.javaClass == validatorClass } as? T

    interface TextValidationElement<in T> {
        fun validate(value: T): Result
        fun validateString(value: String): Result

        sealed class Result {
            data object Valid : Result()
            data class Invalid(val message: String) : Result()
        }

        companion object
    }
}

fun ValidationResult<*>.errorMessages(): List<CharSequence>? =
    if (this is ValidationResult.Invalid<*>) {
        errorMessages
    } else {
        null
    }
