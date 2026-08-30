package com.patrykandpatrick.liftapp.domain.date

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test

class DateIntervalTest {

    @Test
    fun `rolling week includes the whole seventh day`() {
        val week = DateInterval.RollingWeek(firstDayOfWeek = DayOfWeek.MONDAY)

        assertEquals(
            week.periodStartTime.toLocalDate().plusDays(6),
            week.periodEndTime.toLocalDate(),
        )
        assertEquals(LocalTime.MAX, week.periodEndTime.toLocalTime())
    }

    @Test
    fun `A window spans the days between its ends, both included`() {
        assertEquals(7, DateInterval.RollingDays(DAYS_IN_WEEK).periodDays)
        assertEquals(30, DateInterval.RollingDays(30).periodDays)
        assertEquals(7, DateInterval.RollingWeek(firstDayOfWeek = DayOfWeek.MONDAY).periodDays)
        assertEquals(
            LocalDate.now().lengthOfMonth().toLong(),
            DateInterval.RollingMonth().periodDays,
        )
    }

    @Test
    fun `A trailing window has elapsed in full`() {
        // Both of these end today, so nothing in them is still to come.
        assertEquals(
            DateInterval.RollingDays(DAYS_IN_WEEK).periodDays,
            DateInterval.RollingDays(DAYS_IN_WEEK).elapsedDays,
        )
        assertEquals(
            DateInterval.RollingDays(30).periodDays,
            DateInterval.RollingDays(30).elapsedDays,
        )
    }

    @Test
    fun `A window running into the future counts only as far as today`() {
        // The current month ends on a date nobody has reached, so its elapsed days are the days of
        // the month so far — which is what anything averaging over it has to divide by.
        val month = DateInterval.RollingMonth()

        assertEquals(LocalDate.now().dayOfMonth.toLong(), month.elapsedDays)
        assertTrue(month.elapsedDays <= month.periodDays)
    }

    @Test
    fun `A past window has fully elapsed`() {
        val lastMonth = DateInterval.RollingMonth().decrement()

        assertEquals(lastMonth.periodDays, lastMonth.elapsedDays)
    }

    @Test
    fun `The volume options offer no yearly window`() {
        val options = DateInterval.volumeOptions(DayOfWeek.MONDAY)

        // A year averaged down to a week smooths away every block, deload and layoff in it.
        assertTrue(options.none { it is DateInterval.RollingYear })
        assertEquals(DateInterval.RollingDays(DAYS_IN_WEEK), options.first())
    }
}
