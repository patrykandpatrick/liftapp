package com.patrykandpatrick.liftapp.domain.date

import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertEquals
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
}
