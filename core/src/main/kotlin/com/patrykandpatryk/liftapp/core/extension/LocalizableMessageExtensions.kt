package com.patrykandpatryk.liftapp.core.extension

import com.patrykandpatryk.liftapp.domain.validation.Validatable

fun <T> Validatable<T>.getMessageTextOrNull(): String? =
    (this as? Validatable.Invalid)?.message
