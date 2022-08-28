package com.patrykandpatryk.liftapp.domain.date

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

fun SimpleDateFormat.safeParse(source: String): Date? = try {
    parse(source)
} catch (exception: ParseException) {
    null
}

fun SimpleDateFormat.isValid(source: String) = safeParse(source) != null

fun SimpleDateFormat.parseToCalendar(source: String): Calendar =
    Calendar
        .getInstance()
        .apply {
            timeInMillis = parse(source).time
        }

fun SimpleDateFormat.safeParseToCalendar(source: String): Calendar? =
    try {
        parseToCalendar(source)
    } catch (exception: ParseException) {
        null
    }
