package com.patrykandpatryk.liftapp.core.text

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.validation.TextValidator
import com.patrykandpatryk.liftapp.domain.validation.ValidationResult
import com.patrykandpatryk.liftapp.domain.validation.errorMessages
import com.patrykandpatryk.liftapp.domain.value.ValueProvider

@Stable
abstract class TextFieldState<T : Any>(
    initialText: String,
    val textValidator: TextValidator<T>?,
    protected val onTextChange: (String) -> Unit,
    protected val onValueChange: (T) -> Unit,
    protected val veto: (T) -> Boolean,
    protected val enabled: TextFieldState<T>.() -> Boolean,
) : ValueProvider<T> {
    protected val _text = mutableStateOf(initialText)
    protected val _errorMessage = mutableStateOf<String?>(null)

    override val value: T get() = toValue(_text.value) ?: defaultValue
    abstract val defaultValue: T
    val text: String by _text
    val errorMessage: String? by _errorMessage
    val hasError by derivedStateOf { _errorMessage.value != null }

    private val validationResult
        get() = textValidator?.validate(value, text) ?: ValidationResult.Valid(value)

    abstract fun toValue(text: String): T?

    abstract fun toText(value: T): String

    fun updateText(text: String) {
        updateText(text, true)
    }

    fun updateValue(value: T) {
        updateText(toText(value), false)
    }

    private fun updateText(text: String, fromUser: Boolean) {
        val convertedValue = toValue(text) ?: return
        if (veto(convertedValue)) return
        _text.value = text
        updateErrorMessages()
        if (fromUser) {
            onTextChange(text)
            onValueChange(convertedValue)
        }
    }

    fun updateErrorMessages() {
        _errorMessage.value = if (enabled(this)) validationResult.errorMessages()?.first()?.toString() else null
    }

    fun <T> getCondition(validatorClass: Class<T>): T? = textValidator?.getTextValidatorElement(validatorClass)
}

@Stable
class StringTextFieldState(
    initialValue: String = "",
    textValidator: TextValidator<String>? = null,
    onTextChange: (String) -> Unit = {},
    onValueChange: (String) -> Unit = {},
    veto: (String) -> Boolean = { false },
    enabled: TextFieldState<String>.() -> Boolean,
) : TextFieldState<String>(initialValue, textValidator, onTextChange, onValueChange, veto, enabled) {
    override val defaultValue: String = ""

    override fun toValue(text: String): String = text

    override fun toText(value: String): String = value
}

@Stable
class IntTextFieldState(
    initialValue: String = "",
    textValidator: TextValidator<Int>? = null,
    onTextChange: (String) -> Unit = {},
    onValueChange: (Int) -> Unit = {},
    veto: (Int) -> Boolean = { false },
    enabled: TextFieldState<Int>.() -> Boolean,
) : TextFieldState<Int>(initialValue, textValidator, onTextChange, onValueChange, veto, enabled) {
    override val defaultValue: Int = 0

    override fun toValue(text: String): Int? = text.ifBlank { "0" }.toIntOrNull()

    override fun toText(value: Int): String = value.toString()
}

@Stable
class FloatTextFieldState(
    private val formatter: Formatter,
    initialValue: String = "",
    textValidator: TextValidator<Float>? = null,
    onTextChange: (String) -> Unit = {},
    onValueChange: (Float) -> Unit = {},
    veto: (Float) -> Boolean = { false },
    enabled: TextFieldState<Float>.() -> Boolean,
) : TextFieldState<Float>(initialValue, textValidator, onTextChange, onValueChange, veto, enabled) {
    override val defaultValue: Float = 0f

    override fun toValue(text: String): Float = formatter.toFloatOrZero(text)

    override fun toText(value: Float): String = formatter.toInputDecimalNumber(value)
}

inline fun <reified R> TextFieldState<*>.getCondition(): R? = textValidator?.getTextValidatorElement(R::class.java)
