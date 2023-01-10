package com.patrykandpatryk.liftapp.domain.extension

val IntRange.length: Int
    get() = if (start < endInclusive) endInclusive - start else start - endInclusive
