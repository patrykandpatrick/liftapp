package com.patrykandpatrick.liftapp.feature.dashboard.model

import com.patrykandpatrick.liftapp.domain.date.DAYS_IN_WEEK
import com.patrykandpatrick.liftapp.domain.date.startOfWeek
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * The week the dashboard reports on. The day picker and the totals above it both go through here so
 * that they cannot disagree about where the week starts.
 */
object DashboardWeek {

    fun startOf(date: LocalDate, firstDayOfWeek: DayOfWeek): LocalDate =
        date.startOfWeek(firstDayOfWeek)

    fun contains(week: LocalDate, date: LocalDate, firstDayOfWeek: DayOfWeek): Boolean {
        val start = startOf(week, firstDayOfWeek)
        return date >= start && date < start.plusDays(DAYS_IN_WEEK.toLong())
    }
}
