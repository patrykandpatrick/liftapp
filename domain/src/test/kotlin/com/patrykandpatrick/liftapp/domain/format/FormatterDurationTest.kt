package com.patrykandpatrick.liftapp.domain.format

import com.patrykandpatrick.liftapp.testing.TestStringProvider
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class FormatterDurationTest {

    private val formatter = Formatter(TestStringProvider, MutableStateFlow(true))

    @Test
    fun `Hours and minutes are both named`() {
        assertEquals("1 hr 14 min", formatter.formatDurationWithUnits(1.hours + 14.minutes))
    }

    @Test
    fun `A whole number of hours does not mention minutes`() {
        assertEquals("2 hr", formatter.formatDurationWithUnits(2.hours))
    }

    @Test
    fun `Under an hour only minutes are reported`() {
        assertEquals("45 min", formatter.formatDurationWithUnits(45.minutes))
    }

    @Test
    fun `Seconds show only when there is nothing larger`() {
        assertEquals("30 sec", formatter.formatDurationWithUnits(30.seconds))
        assertEquals("1 min", formatter.formatDurationWithUnits(1.minutes + 30.seconds))
    }

    @Test
    fun `Nothing recorded reads as no seconds rather than as empty`() {
        assertEquals("0 sec", formatter.formatDurationWithUnits(Duration.ZERO))
    }

    @Test
    fun `Hours are not rolled up into days`() {
        assertEquals("31 hr 12 min", formatter.formatDurationWithUnits(31.hours + 12.minutes))
    }
}
