package com.patrykandpatryk.liftapp.domain.extension

import com.patrykandpatryk.liftapp.domain.unit.ValueUnit

fun getTypeErrorMessage(unit: ValueUnit): String = "Unsupported type of ${unit::class.qualifiedName}."
