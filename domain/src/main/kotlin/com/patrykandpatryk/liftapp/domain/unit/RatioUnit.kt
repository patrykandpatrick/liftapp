package com.patrykandpatryk.liftapp.domain.unit

interface RatioUnit<Dividend : ValueUnit, Divisor : ValueUnit> : ValueUnit {
    val dividend: Dividend
    val divisor: Divisor
}
