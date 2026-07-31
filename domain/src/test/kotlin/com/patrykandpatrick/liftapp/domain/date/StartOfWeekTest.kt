package com.patrykandpatrick.liftapp.domain.date

import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.test.assertEquals
import org.junit.Test

class StartOfWeekTest {

    @Test
    fun `A date on the first day of the week starts its own week`() {
        assertEquals(MONDAY, MONDAY.startOfWeek(DayOfWeek.MONDAY))
        assertEquals(SUNDAY, SUNDAY.startOfWeek(DayOfWeek.SUNDAY))
    }

    @Test
    fun `The week is walked back to the first day, never forward`() {
        assertEquals(MONDAY, WEDNESDAY.startOfWeek(DayOfWeek.MONDAY))
        assertEquals(SUNDAY, WEDNESDAY.startOfWeek(DayOfWeek.SUNDAY))
        assertEquals(SATURDAY.minusDays(7), WEDNESDAY.startOfWeek(DayOfWeek.SATURDAY))
    }

    @Test
    fun `Every first day of the week yields a week of seven days containing the date`() {
        DayOfWeek.entries.forEach { firstDayOfWeek ->
            val start = WEDNESDAY.startOfWeek(firstDayOfWeek)

            assertEquals(firstDayOfWeek, start.dayOfWeek)
            assertEquals(true, WEDNESDAY >= start && WEDNESDAY < start.plusDays(7))
        }
    }

    private companion object {
        val SUNDAY: LocalDate = LocalDate.of(2026, 7, 19)
        val MONDAY: LocalDate = LocalDate.of(2026, 7, 20)
        val WEDNESDAY: LocalDate = LocalDate.of(2026, 7, 22)
        val SATURDAY: LocalDate = LocalDate.of(2026, 7, 25)
    }
}
