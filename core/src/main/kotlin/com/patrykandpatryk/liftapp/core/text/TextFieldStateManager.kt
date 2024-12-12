package com.patrykandpatryk.liftapp.core.text

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.validation.TextValidationElementProvider
import com.patrykandpatryk.liftapp.domain.validation.TextValidator
import java.util.LinkedList
import javax.inject.Inject

class TextFieldStateManager
@Inject
constructor(
    private val stringProvider: StringProvider,
    private val formatter: Formatter,
    private val savedStateHandle: SavedStateHandle,
) {
    private val textFields = LinkedList<TextFieldState<*>>()

    private var generatedKeyIndex = 0

    private fun generateSavedStateKey(): String = "text_field_state_${generatedKeyIndex++}"

    fun stringTextField(
        initialValue: String = "",
        validators: TextValidationElementProvider<String>.() -> Unit = {},
        savedStateKey: String = generateSavedStateKey(),
        onTextChange: (String) -> Unit = {},
        onValueChange: (String) -> Unit = {},
        veto: (String) -> Boolean = { false },
        enabled: TextFieldState<String>.() -> Boolean = { true },
    ): StringTextFieldState =
        StringTextFieldState(
                initialValue = savedStateHandle[savedStateKey] ?: initialValue,
                textValidator = validator(validators),
                onTextChange = {
                    savedStateHandle[savedStateKey] = it
                    onTextChange(it)
                },
                onValueChange = onValueChange,
                veto = veto,
                enabled = enabled,
            )
            .also { textFields.add(it) }

    fun intTextField(
        initialValue: String = "",
        validators: TextValidationElementProvider<Int>.() -> Unit = {},
        savedStateKey: String = generateSavedStateKey(),
        onTextChange: (String) -> Unit = {},
        onValueChange: (Int) -> Unit = {},
        veto: (Int) -> Boolean = { false },
        enabled: TextFieldState<Int>.() -> Boolean = { true },
    ): IntTextFieldState =
        IntTextFieldState(
                initialValue = savedStateHandle[savedStateKey] ?: initialValue,
                textValidator = validator(validators),
                onTextChange = {
                    savedStateHandle[savedStateKey] = it
                    onTextChange(it)
                },
                onValueChange = onValueChange,
                veto = veto,
                enabled = enabled,
            )
            .also { textFields.add(it) }

    fun longTextField(
        initialValue: String = "",
        validators: TextValidationElementProvider<Long>.() -> Unit = {},
        savedStateKey: String = generateSavedStateKey(),
        onTextChange: (String) -> Unit = {},
        onValueChange: (Long) -> Unit = {},
        veto: (Long) -> Boolean = { false },
        enabled: TextFieldState<Long>.() -> Boolean = { true },
    ): LongTextFieldState =
        LongTextFieldState(
                initialValue = savedStateHandle[savedStateKey] ?: initialValue,
                textValidator = validator(validators),
                onTextChange = {
                    savedStateHandle[savedStateKey] = it
                    onTextChange(it)
                },
                onValueChange = onValueChange,
                veto = veto,
                enabled = enabled,
            )
            .also { textFields.add(it) }

    fun doubleTextField(
        initialValue: String = "",
        validators: TextValidationElementProvider<Double>.() -> Unit = {},
        savedStateKey: String = generateSavedStateKey(),
        onTextChange: (String) -> Unit = {},
        onValueChange: (Double) -> Unit = {},
        veto: (Double) -> Boolean = { false },
        enabled: TextFieldState<Double>.() -> Boolean = { true },
    ): DoubleTextFieldState =
        DoubleTextFieldState(
                initialValue = savedStateHandle[savedStateKey] ?: initialValue,
                textValidator = validator(validators),
                onValueChange = onValueChange,
                onTextChange = {
                    savedStateHandle[savedStateKey] = it
                    onTextChange(it)
                },
                veto = veto,
                formatter = formatter,
                enabled = enabled,
            )
            .also { textFields.add(it) }

    fun hasErrors(): Boolean {
        textFields.forEach { it.updateErrorMessages() }
        return textFields.any { it.hasError }
    }

    private fun <T> validator(
        conditions: TextValidationElementProvider<T>.() -> Unit
    ): TextValidator<T> {
        val provider = TextValidationElementProviderImpl<T>()
        provider.apply(conditions)
        return TextValidator(
            typeTextValidationElements = provider.typeTextValidationElements,
            stringTextValidationElements = provider.stringTextValidationElements,
        )
    }

    private inner class TextValidationElementProviderImpl<T> :
        TextValidationElementProvider<T>(stringProvider, formatter) {
        public override val typeTextValidationElements:
            MutableSet<TextValidator.TextValidationElement<T>> =
            super.typeTextValidationElements
        public override val stringTextValidationElements:
            MutableSet<TextValidator.TextValidationElement<String>> =
            super.stringTextValidationElements
    }
}
