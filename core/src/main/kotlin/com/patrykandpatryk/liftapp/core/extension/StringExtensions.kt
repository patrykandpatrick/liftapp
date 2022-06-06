package com.patrykandpatryk.liftapp.core.extension

private fun String.prepForConversionToNumber() =
    replace(oldChar = ',', newChar = '.').replace(oldValue = " ", newValue = "")

fun String.smartToIntOrNull() = prepForConversionToNumber().toIntOrNull()

fun String.smartToFloatOrNull() = prepForConversionToNumber().toFloatOrNull()
