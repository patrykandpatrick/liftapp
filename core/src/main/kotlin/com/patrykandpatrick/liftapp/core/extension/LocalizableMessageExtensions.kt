package com.patrykandpatrick.liftapp.core.extension

import com.patrykandpatrick.liftapp.domain.validation.Validatable

fun <T> Validatable<T>.getMessageTextOrNull(): String? = (this as? Validatable.Invalid)?.message
