package com.patrykandpatryk.liftapp.domain.extension

import java.text.DecimalFormat
import java.text.ParseException

fun DecimalFormat.parseFloatOrNull(source: String): Float? = try {
    parse(source).toFloat()
} catch (exception: ParseException) {
    null
}
