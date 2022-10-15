package com.patrykandpatryk.liftapp.core.validation

import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.testing.TestStringProvider
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

private const val BASE_NAME = "A name"

class NameValidatorTest {

    private val stringProvider = TestStringProvider
    private val validator = NameValidator(stringProvider = stringProvider)

    @Test
    fun `Given name is not empty and not too long the name is valid`() {
        val result = validator.validate(BASE_NAME)
        assertIs<Validatable.Valid<String>>(result)
    }

    @Test
    fun `Given name is empty the name is invalid`() {
        val result = validator.validate(" ")
        assertIs<Validatable.Invalid<String>>(result)
        assertEquals(
            expected = stringProvider.getErrorCannotBeEmpty(stringProvider.name),
            actual = result.message,
        )
    }

    @Test
    fun `Given name is blank the name is invalid`() {
        val result = validator.validate("")
        assertIs<Validatable.Invalid<String>>(result)
        assertEquals(
            expected = stringProvider.getErrorCannotBeEmpty(stringProvider.name),
            actual = result.message,
        )
    }

    @Test
    fun `Given name is too long the name is invalid`() {
        val tooLongName = buildString {
            repeat(Constants.Input.NAME_MAX_CHARS + 1) {
                append("a")
            }
        }

        val result = validator.validate(tooLongName)
        assertIs<Validatable.Invalid<String>>(result)
        assertEquals(
            expected = stringProvider.getErrorNameTooLong(tooLongName.length),
            actual = result.message,
        )
    }
}
