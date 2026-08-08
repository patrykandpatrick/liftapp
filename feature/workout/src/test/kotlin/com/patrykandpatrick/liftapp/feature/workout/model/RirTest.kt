package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatrick.liftapp.core.text.IntTextFieldState
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test

/**
 * The selector stores its answer as text, because that is the only representation in which "not
 * reported" and "taken to failure" are different things.
 */
class RirTest {

    private fun field(text: String = "") = IntTextFieldState(initialValue = text)

    @Test
    fun `a blank field reports no rir`() {
        assertNull(field().recordedRir)
    }

    @Test
    fun `zero is a reported value, not an absent one`() {
        assertEquals(0, field("0").recordedRir)
    }

    @Test
    fun `text that is not a number reports no rir`() {
        assertNull(field("-").recordedRir)
        assertNull(field("many").recordedRir)
    }

    @Test
    fun `a value from outside the range is clamped rather than reported as is`() {
        assertEquals(4, field("9").recordedRir)
        assertEquals(0, field("-2").recordedRir)
    }

    @Test
    fun `selecting an option records it`() {
        val field = field()

        field.toggleRir(2)

        assertEquals(2, field.recordedRir)
        assertEquals("2", field.text)
    }

    @Test
    fun `selecting the recorded option again clears the field`() {
        val field = field("2")

        field.toggleRir(2)

        assertNull(field.recordedRir)
        assertEquals("", field.text)
    }

    @Test
    fun `selecting a different option replaces the recorded one`() {
        val field = field("2")

        field.toggleRir(0)

        assertEquals(0, field.recordedRir)
    }

    @Test
    fun `clearing and reselecting zero round-trips, rather than sticking on empty`() {
        val field = field()

        field.toggleRir(0)
        assertEquals(0, field.recordedRir)

        field.toggleRir(0)
        assertNull(field.recordedRir)

        field.toggleRir(0)
        assertEquals(0, field.recordedRir)
    }

    @Test
    fun `applying a set without a reported rir clears the field`() {
        val field = field("3")

        field.updateRir(null)

        assertNull(field.recordedRir)
    }

    @Test
    fun `applying a set clamps what it applies`() {
        val field = field()

        field.updateRir(9)

        assertEquals(4, field.recordedRir)
    }
}
