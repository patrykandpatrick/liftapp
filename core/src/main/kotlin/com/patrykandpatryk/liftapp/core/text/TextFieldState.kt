package com.patrykandpatryk.liftapp.core.text

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.validation.TextValidator
import com.patrykandpatryk.liftapp.domain.validation.ValidationResult
import com.patrykandpatryk.liftapp.domain.validation.errorMessages
import com.patrykandpatryk.liftapp.domain.value.ValueProvider
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Stable
abstract class TextFieldState<T : Any>(
    initialText: String,
    val textValidator: TextValidator<T>?,
    protected val onTextChange: (String) -> Unit,
    protected val onValueChange: (T?) -> Unit,
    protected val veto: (T) -> Boolean,
    protected val enabled: TextFieldState<T>.() -> Boolean,
) : ValueProvider<T> {
    var textFieldValue by mutableStateOf(TextFieldValue(initialText))
        protected set

    protected val _errorMessage = mutableStateOf<String?>(null)

    override val value: T
        get() = toValue(text) ?: defaultValue

    abstract val defaultValue: T

    open val emptyTextReplacement: String = ""

    val text: String
        get() = textFieldValue.text

    val errorMessage: String? by _errorMessage

    val hasError by derivedStateOf { _errorMessage.value != null }

    val isValid: Boolean
        get() = validationResult is ValidationResult.Valid

    private val validationResult
        get() =
            textValidator?.validate(value, text.ifEmpty { emptyTextReplacement })
                ?: ValidationResult.Valid(value)

    abstract fun toValue(text: String): T?

    abstract fun toText(value: T): String

    fun updateText(text: String) {
        updateText(textFieldValue = textFieldValue.copy(text), fromUser = true)
    }

    fun updateText(textFieldValue: TextFieldValue) {
        updateText(textFieldValue = textFieldValue, fromUser = true)
    }

    fun updateValue(value: T) {
        val text = toText(value)
        updateText(
            textFieldValue = textFieldValue.copy(text = text, selection = TextRange(text.length)),
            fromUser = true,
        )
    }

    private fun updateText(textFieldValue: TextFieldValue, fromUser: Boolean) {
        val convertedValue = toValue(textFieldValue.text) ?: return
        if (veto(convertedValue)) return
        this.textFieldValue = textFieldValue
        updateErrorMessages()
        if (fromUser) {
            onTextChange(text)
            onValueChange(convertedValue)
        }
    }

    fun updateErrorMessages() {
        _errorMessage.value =
            if (enabled(this)) validationResult.errorMessages()?.first()?.toString() else null
    }

    fun <T> getCondition(validatorClass: Class<T>): T? =
        textValidator?.getTextValidatorElement(validatorClass)

    fun clear() {
        textFieldValue = TextFieldValue()
        _errorMessage.value = null
        onTextChange(text)
        onValueChange(null)
    }

    companion object {
        internal const val MINUS_SIGN = "-"
    }
}

@Stable
class StringTextFieldState(
    initialValue: String = "",
    textValidator: TextValidator<String>? = null,
    onTextChange: (String) -> Unit = {},
    onValueChange: (String?) -> Unit = {},
    veto: (String) -> Boolean = { false },
    enabled: TextFieldState<String>.() -> Boolean = { true },
) :
    TextFieldState<String>(
        initialValue,
        textValidator,
        onTextChange,
        onValueChange,
        veto,
        enabled,
    ) {
    override val defaultValue: String = ""

    override fun toValue(text: String): String = text

    override fun toText(value: String): String = value
}

@Stable
class IntTextFieldState(
    initialValue: String = "",
    textValidator: TextValidator<Int>? = null,
    onTextChange: (String) -> Unit = {},
    onValueChange: (Int?) -> Unit = {},
    veto: (Int) -> Boolean = { false },
    enabled: TextFieldState<Int>.() -> Boolean = { true },
) : TextFieldState<Int>(initialValue, textValidator, onTextChange, onValueChange, veto, enabled) {
    override val defaultValue: Int = 0

    override val emptyTextReplacement: String = "0"

    override fun toValue(text: String) =
        if (text.isBlank() || text == MINUS_SIGN) 0 else text.toIntOrNull()

    override fun toText(value: Int): String = value.toString()
}

@Stable
class LongTextFieldState(
    initialValue: String = "",
    textValidator: TextValidator<Long>? = null,
    onTextChange: (String) -> Unit = {},
    onValueChange: (Long?) -> Unit = {},
    veto: (Long) -> Boolean = { false },
    enabled: TextFieldState<Long>.() -> Boolean = { true },
) : TextFieldState<Long>(initialValue, textValidator, onTextChange, onValueChange, veto, enabled) {
    override val defaultValue: Long = 0

    override val emptyTextReplacement: String = "0"

    override fun toValue(text: String) =
        if (text.isBlank() || text == MINUS_SIGN) 0L else text.toLongOrNull()

    override fun toText(value: Long): String = value.toString()
}

@Stable
class DoubleTextFieldState(
    private val formatter: Formatter,
    initialValue: String = "",
    textValidator: TextValidator<Double>? = null,
    onTextChange: (String) -> Unit = {},
    onValueChange: (Double?) -> Unit = {},
    veto: (Double) -> Boolean = { false },
    enabled: TextFieldState<Double>.() -> Boolean = { true },
) :
    TextFieldState<Double>(
        initialValue,
        textValidator,
        onTextChange,
        onValueChange,
        veto,
        enabled,
    ) {
    override val defaultValue: Double = 0.0

    override val emptyTextReplacement: String = "0"

    override fun toValue(text: String) =
        if (text.isBlank() || text == MINUS_SIGN) 0.0 else formatter.toDoubleOrNull(text)

    override fun toText(value: Double): String = formatter.toInputDecimalNumber(value)
}

@Stable
class LocalDateTextFieldState(
    private val formatter: DateTimeFormatter,
    initialValue: String = "",
    textValidator: TextValidator<LocalDate>? = null,
    onTextChange: (String) -> Unit = {},
    onValueChange: (LocalDate?) -> Unit = {},
    veto: (LocalDate) -> Boolean = { false },
    enabled: TextFieldState<LocalDate>.() -> Boolean = { true },
) :
    TextFieldState<LocalDate>(
        initialValue,
        textValidator,
        onTextChange,
        onValueChange,
        veto,
        enabled,
    ) {
    override val defaultValue: LocalDate = LocalDate.now()

    override fun toValue(text: String) =
        if (text.isBlank()) null else LocalDate.parse(text, formatter)

    override fun toText(value: LocalDate): String = formatter.format(value)
}

@Stable
class LocalTimeTextFieldState(
    private val formatter: DateTimeFormatter,
    initialValue: String = "",
    textValidator: TextValidator<LocalTime>? = null,
    onTextChange: (String) -> Unit = {},
    onValueChange: (LocalTime?) -> Unit = {},
    veto: (LocalTime) -> Boolean = { false },
    enabled: TextFieldState<LocalTime>.() -> Boolean = { true },
) :
    TextFieldState<LocalTime>(
        initialValue,
        textValidator,
        onTextChange,
        onValueChange,
        veto,
        enabled,
    ) {
    override val defaultValue: LocalTime = LocalTime.now()

    override fun toValue(text: String) = LocalTime.parse(text, formatter)

    override fun toText(value: LocalTime): String = formatter.format(value)
}

inline fun <reified R> TextFieldState<*>.getCondition(): R? =
    textValidator?.getTextValidatorElement(R::class.java)

fun TextFieldState<Double>.updateValueBy(value: Double) {
    updateValue(this.value + value)
}

fun TextFieldState<Int>.updateValueBy(value: Int) {
    updateValue(this.value + value)
}
