package com.patrykandpatryk.liftapp.domain.date

import java.time.LocalDate
import java.time.LocalDateTime

fun LocalDate.atStartOfWeek(): LocalDateTime = minusDays(dayOfWeek.ordinal.toLong()).atStartOfDay()

fun LocalDate.atEndOfDay(): LocalDateTime = plusDays(1).atStartOfDay().minusNanos(1)

fun LocalDate.atEndOfMonth(): LocalDate = withDayOfMonth(month.length(isLeapYear))

fun LocalDate.atEndOfYear(): LocalDate = withDayOfYear(lengthOfYear())
