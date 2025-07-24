package com.patrykandpatryk.liftapp.domain

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <T> runIf(condition: Boolean, block: () -> T): T? {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return if (condition) block() else null
}
