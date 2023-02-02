package com.patrykandpatryk.liftapp.domain.extension

const val LIST_SEPARATOR = ", "

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
