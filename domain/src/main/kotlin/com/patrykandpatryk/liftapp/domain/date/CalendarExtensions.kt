package com.patrykandpatryk.liftapp.domain.date

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

var Calendar.second: Int
    get() = get(Calendar.SECOND)
    set(value) {
        set(Calendar.SECOND, value)
    }

var Calendar.minute: Int
    get() = get(Calendar.MINUTE)
    set(value) {
        set(Calendar.MINUTE, value)
    }

var Calendar.hour: Int
    get() = get(Calendar.HOUR_OF_DAY)
    set(value) {
        set(Calendar.HOUR_OF_DAY, value)
    }

var Calendar.day: Int
    get() = get(Calendar.DAY_OF_MONTH)
    set(value) {
        set(Calendar.DAY_OF_MONTH, value)
    }

var Calendar.month: Int
    get() = get(Calendar.MONTH)
    set(value) {
        set(Calendar.MONTH, value)
    }

var Calendar.year: Int
    get() = get(Calendar.YEAR)
    set(value) {
        set(Calendar.YEAR, value)
    }

val Calendar.secondString: String
    get() = second.toString()

val Calendar.minuteString: String
    get() = minute.toString()

val Calendar.hourString: String
    get() = hour.toString()

fun getMillisFor(
    year: Int = 0,
    month: Int = 0,
    day: Int = 0,
    hour: Int = 0,
    minute: Int = 0,
    second: Int = 0,
): Long =
    Calendar
        .getInstance()
        .apply {
            this.year = year
            this.month = month
            this.day = day
            this.hour = hour
            this.minute = minute
            this.second = second
        }.timeInMillis

fun Long.millisToCalendar(): Calendar =
    Calendar
        .getInstance()
        .also { it.timeInMillis = this }

fun Calendar.toLocalDateTime(): LocalDateTime =
    LocalDateTime.from(Instant.ofEpochMilli(timeInMillis).atZone(ZoneId.of("UTC")))
