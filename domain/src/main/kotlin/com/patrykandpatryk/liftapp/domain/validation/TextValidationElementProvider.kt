package com.patrykandpatryk.liftapp.domain.validation

import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.value.ValueProvider

abstract class TextValidationElementProvider<T>(
    val stringProvider: StringProvider,
    val formatter: Formatter,
) {
    protected open val typeTextValidationElements = mutableSetOf<TextValidator.TextValidationElement<T>>()
    protected open val stringTextValidationElements = mutableSetOf<TextValidator.TextValidationElement<String>>()

    fun addCondition(textValidationElement: TextValidator.TextValidationElement<T>) {
        typeTextValidationElements.add(textValidationElement)
    }

    fun addStringCondition(textValidationElement: TextValidator.TextValidationElement<String>) {
        stringTextValidationElements.add(textValidationElement)
    }
}

fun TextValidationElementProvider<*>.nonEmpty() {
    addStringCondition(NonEmptyTextValidationElement(stringProvider))
}

fun TextValidationElementProvider<String>.requireLength(
    minLength: Int? = null,
    maxLength: Int? = null,
) {
    addCondition(RequiredLengthTextValidationElement(minLength, maxLength, stringProvider))
}

fun TextValidationElementProvider<*>.validNumber() {
    addStringCondition(ValidNumberTextValidationElement(stringProvider, formatter))
}

fun <T : Number> TextValidationElementProvider<T>.valueInRange(min: Double? = null, max: Double? = null) {
    addCondition(ValueRangeTextValidationElement(stringProvider, formatter, min, max))
}

fun <T : Number> TextValidationElementProvider<T>.valueInRange(range: ClosedFloatingPointRange<Double>) {
    valueInRange(range.start, range.endInclusive)
}

fun <T : Number> TextValidationElementProvider<T>.higherThanZero() {
    addCondition(
        ValueRangeTextValidationElement(
            minValue = 0.0,
            stringProvider = stringProvider,
            formatter = formatter,
        )
    )
}

fun <T : Number> TextValidationElementProvider<T>.validNumberHigherThanZero() {
    validNumber()
    higherThanZero()
}

fun <T : Number> TextValidationElementProvider<T>.isMultiplyOf(vararg valueProviders: ValueProvider<Number>) {
    addCondition(IsMultiplyOf(stringProvider, formatter, valueProviders))
}