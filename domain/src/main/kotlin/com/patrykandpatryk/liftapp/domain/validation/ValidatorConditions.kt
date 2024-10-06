package com.patrykandpatryk.liftapp.domain.validation

import com.patrykandpatryk.liftapp.domain.extension.toDoubleOrZero
import com.patrykandpatryk.liftapp.domain.extension.toIntOrZero
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.value.ValueProvider

data class NonEmptyTextValidationElement(val stringProvider: StringProvider) : TextValidator.TextValidationElement<String> {
    override fun validate(value: String): TextValidator.TextValidationElement.Result = if (value.isBlank()) {
        TextValidator.TextValidationElement.Result.Invalid(stringProvider.fieldCannotBeEmpty())
    } else {
        TextValidator.TextValidationElement.Result.Valid
    }

    override fun validateString(value: String): TextValidator.TextValidationElement.Result = validate(value)
}

data class RequiredLengthTextValidationElement(
    var minValue: Int?,
    var maxValue: Int?,
    val stringProvider: StringProvider,
) : TextValidator.TextValidationElement<String> {
    override fun validate(value: String): TextValidator.TextValidationElement.Result {
        val minValue = minValue
        val maxValue = maxValue
        return when {
            minValue != null && value.length < minValue -> {
                TextValidator.TextValidationElement.Result.Invalid(
                    stringProvider.fieldTooShort(value.length, minValue)
                )
            }

            maxValue != null && value.length > maxValue -> {
                TextValidator.TextValidationElement.Result.Invalid(
                    stringProvider.fieldTooLong(value.length, maxValue)
                )
            }

            else -> {
                TextValidator.TextValidationElement.Result.Valid
            }
        }
    }

    override fun validateString(value: String): TextValidator.TextValidationElement.Result = validate(value)
}

data class ValueRangeTextValidationElement(
    private val stringProvider: StringProvider,
    private val formatter: Formatter,
    var minValue: Double? = null,
    var maxValue: Double? = null,
) : TextValidator.TextValidationElement<Number> {
    override fun validate(value: Number): TextValidator.TextValidationElement.Result {
        val minValue = minValue
        val maxValue = maxValue
        return when {
            minValue != null && value.toDouble() <= minValue -> {
                TextValidator.TextValidationElement.Result.Invalid(
                    stringProvider.valueTooSmall(
                        formatter.formatNumber(
                            minValue, format = Formatter.NumberFormat.Decimal
                        )
                    )
                )
            }

            maxValue != null && value.toDouble() >= maxValue -> {
                TextValidator.TextValidationElement.Result.Invalid(
                    stringProvider.valueTooBig(
                        formatter.formatNumber(
                            maxValue, format = Formatter.NumberFormat.Decimal
                        )
                    )
                )
            }

            else -> {
                TextValidator.TextValidationElement.Result.Valid
            }
        }
    }

    override fun validateString(value: String): TextValidator.TextValidationElement.Result =
        validate(value.toIntOrZero())
}

data class ValidNumberTextValidationElement(
    private val stringProvider: StringProvider,
    private val formatter: Formatter,
) : TextValidator.TextValidationElement<String> {
    override fun validate(value: String): TextValidator.TextValidationElement.Result =
        if (formatter.isValidNumber(value)) {
            TextValidator.TextValidationElement.Result.Valid
        } else {
            TextValidator.TextValidationElement.Result.Invalid(stringProvider.valueNotValidNumber())
        }

    override fun validateString(value: String): TextValidator.TextValidationElement.Result = validate(value)
}

class IsMultiplyOf(
    private val stringProvider: StringProvider,
    private val formatter: Formatter,
    private val fields: Array<out ValueProvider<Number>>,
) : TextValidator.TextValidationElement<Number> {
    override fun validate(value: Number): TextValidator.TextValidationElement.Result {
        var expectedValue = fields.fold(1.0) { acc, valueProvider -> acc * valueProvider.value.toDouble() }
        expectedValue = formatter.round(expectedValue)
        return if (formatter.round(value.toDouble()) == expectedValue) {
            TextValidator.TextValidationElement.Result.Valid
        } else {
            TextValidator.TextValidationElement.Result.Invalid(
                stringProvider.doesNotEqual(
                    fields.joinToString(separator = " × ") { formatter.formatNumber(it.value, format = Formatter.NumberFormat.Decimal) },
                    formatter.formatNumber(value, format = Formatter.NumberFormat.Decimal),
                )
            )
        }
    }

    override fun validateString(value: String): TextValidator.TextValidationElement.Result =
        validate(value.toDoubleOrZero())
}

class IsHigherOrEqualTo(
    private val stringProvider: StringProvider,
    private val formatter: Formatter,
    private val fields: Array<out ValueProvider<Number>>,
) : TextValidator.TextValidationElement<Number> {
    override fun validate(value: Number): TextValidator.TextValidationElement.Result {
        val expectedValue = fields.fold(0.0) { acc, valueProvider -> acc + valueProvider.value.toDouble() }
        return if (value.toDouble() >= expectedValue) {
            TextValidator.TextValidationElement.Result.Valid
        } else {
            TextValidator.TextValidationElement.Result.Invalid(
                stringProvider.fieldMustBeHigherOrEqualTo(
                    formatter.formatNumber(expectedValue, format = Formatter.NumberFormat.Decimal)
                )
            )
        }
    }

    override fun validateString(value: String): TextValidator.TextValidationElement.Result =
        validate(value.toDoubleOrZero())
}

