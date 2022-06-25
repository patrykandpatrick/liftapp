package com.patrykandpatryk.liftapp.core.extension

const val LIST_SEPARATOR = ", "

private fun String.prepForConversionToNumber() =
    replace(oldChar = ',', newChar = '.').replace(oldValue = " ", newValue = "")

fun String.smartToIntOrNull() = prepForConversionToNumber().toIntOrNull()

fun String.smartToFloatOrNull() = prepForConversionToNumber().toFloatOrNull()

inline fun <T> Collection<T>.joinToPrettyString(
    andText: CharSequence,
    toString: (T) -> String,
): String {
    val builder = StringBuilder()

    for ((index, element) in withIndex()) {
        builder.append(toString(element))

        if (index < size - 1) {
            if (index == size - 2) {
                builder.append(" $andText ")
            } else {
                builder.append(LIST_SEPARATOR)
            }
        }
    }

    return builder.toString()
}
