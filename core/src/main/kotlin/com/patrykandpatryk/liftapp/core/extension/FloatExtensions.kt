package com.patrykandpatryk.liftapp.core.extension

import kotlin.math.pow
import kotlin.math.round

fun Float.round(decimalPlaces: Int): Float {
    val multiplier = 1f * 10f.pow(n = decimalPlaces)
    return round(x = this * multiplier) / multiplier
}
