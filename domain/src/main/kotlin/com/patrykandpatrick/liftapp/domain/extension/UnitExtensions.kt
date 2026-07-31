package com.patrykandpatrick.liftapp.domain.extension

import com.patrykandpatrick.liftapp.domain.unit.ValueUnit

fun getTypeErrorMessage(unit: ValueUnit): String =
    "Unsupported type of ${unit::class.qualifiedName}."
