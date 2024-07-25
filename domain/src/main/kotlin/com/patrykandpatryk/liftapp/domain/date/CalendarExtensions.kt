package com.patrykandpatryk.liftapp.domain.date

import java.util.Calendar

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
