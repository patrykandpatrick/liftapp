package com.patrykandpatryk.liftapp.core.validation

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.validation.nonEmpty
import com.patrykandpatryk.liftapp.domain.validation.requireLength
import com.patrykandpatryk.liftapp.testing.TestStringProvider
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

private const val BASE_NAME = "A name"

class NameTextValidatorTest {
    private val stringProvider = TestStringProvider
    private val textFieldStateManager =
        TextFieldStateManager(
            stringProvider,
            Formatter(stringProvider, MutableStateFlow(false)),
            SavedStateHandle(),
        )
    private val textField =
        textFieldStateManager.stringTextField(
            validators = {
                nonEmpty()
                requireLength(maxLength = Constants.Input.NAME_MAX_CHARS)
            }
        )

    @Test
    fun `Given na me is not empty and not too long the name is valid`() {
        textField.updateText(BASE_NAME)
        assertFalse(textField.hasError)
    }

    @Test
    fun `Given name is blank the name is invalid`() {
        textField.updateText(" ")
        assertTrue(textField.hasError)
        assertEquals(
            expected = stringProvider.fieldCannotBeEmpty(),
            actual = textField.errorMessage,
        )
    }

    @Test
    fun `Given name is empty the name is invalid`() {
        textField.updateText("")
        textField.updateErrorMessages()
        assertTrue(textField.hasError)
        assertEquals(
            expected = stringProvider.fieldCannotBeEmpty(),
            actual = textField.errorMessage,
        )
    }

    @Test
    fun `Given name is too long the name is invalid`() {
        val tooLongName = buildString { repeat(Constants.Input.NAME_MAX_CHARS + 1) { append("a") } }

        textField.updateText(tooLongName)
        assertTrue(textField.hasError)
        assertEquals(
            expected =
                stringProvider.fieldTooLong(tooLongName.length, Constants.Input.NAME_MAX_CHARS),
            actual = textField.errorMessage,
        )
    }
}
