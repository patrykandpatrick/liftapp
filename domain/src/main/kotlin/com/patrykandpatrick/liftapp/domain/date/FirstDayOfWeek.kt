package com.patrykandpatrick.liftapp.domain.date

import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.Locale

/** The days a week can be set to begin on, in the order the published app listed them. */
val firstDayOfWeekOptions = listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)

/**
 * The locale's own first day of the week, which the published app also defaulted to. Locales naming
 * a day outside [firstDayOfWeekOptions] fall back to Monday, so that the setting always shows the
 * day it is on.
 */
fun getDefaultFirstDayOfWeek(locale: Locale = Locale.getDefault()): DayOfWeek =
    WeekFields.of(locale).firstDayOfWeek.takeIf { it in firstDayOfWeekOptions } ?: DayOfWeek.MONDAY
