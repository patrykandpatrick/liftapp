package com.patrykandpatryk.liftapp.domain.math

fun Float.subFraction(from: Float, to: Float): Float =
    ((this - from) / (to - from)).coerceIn(0f, 1f)
