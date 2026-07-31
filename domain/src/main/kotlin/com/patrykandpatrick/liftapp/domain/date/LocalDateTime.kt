package com.patrykandpatrick.liftapp.domain.date

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

const val DAYS_IN_WEEK = 7

/**
 * The date the week containing this one began on, for a week that starts on [firstDayOfWeek]. Every
 * week the app lays out or totals goes through here, so that none of them can disagree about where
 * a week starts.
 */
fun LocalDate.startOfWeek(firstDayOfWeek: DayOfWeek): LocalDate =
    minusDays(((dayOfWeek.value - firstDayOfWeek.value + DAYS_IN_WEEK) % DAYS_IN_WEEK).toLong())

fun LocalDate.atStartOfWeek(firstDayOfWeek: DayOfWeek): LocalDateTime =
    startOfWeek(firstDayOfWeek).atStartOfDay()

fun LocalDate.atEndOfDay(): LocalDateTime = plusDays(1).atStartOfDay().minusNanos(1)

fun LocalDate.atEndOfMonth(): LocalDate = withDayOfMonth(month.length(isLeapYear))

fun LocalDate.atEndOfYear(): LocalDate = withDayOfYear(lengthOfYear())
