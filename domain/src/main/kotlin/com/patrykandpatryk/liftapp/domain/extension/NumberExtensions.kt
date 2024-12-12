package com.patrykandpatryk.liftapp.domain.extension

import java.text.DecimalFormat
import java.text.ParseException

fun String.toIntOrZero(): Int = toIntOrNull() ?: 0

fun DecimalFormat.parseToIntOrNull(source: String): Int? =
    try {
        parse(source).toInt()
    } catch (exception: ParseException) {
        null
    }

fun DecimalFormat.parseToIntOrZero(source: String): Int = parseToIntOrNull(source) ?: 0

fun Int.rangeOfLength(length: Int) = this..(this + length)
