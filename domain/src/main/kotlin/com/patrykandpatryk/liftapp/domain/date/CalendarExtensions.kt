package com.patrykandpatryk.liftapp.domain.date

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

val Calendar.secondString: String
    get() = second.toString()

val Calendar.minuteString: String
    get() = minute.toString()

val Calendar.hourString: String
    get() = hour.toString()
