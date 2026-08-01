package com.patrykandpatrick.liftapp.domain.bodymeasurement

/**
 * A reading split into its parts, so a caller can lay the number, its unit, and the change out
 * separately rather than receiving one pre-joined string.
 * [FormatBodyMeasurementValueToStringUseCase] covers the joined case.
 */
data class BodyMeasurementValueDisplay(
    val primary: String,
    val secondary: String?,
    val unit: String,
    val delta: Delta?,
) {
    /**
     * The change since the previous entry, in the same [unit] as the value. [label] carries its own
     * sign so the direction survives without color, which [direction] is only a shortcut for.
     */
    data class Delta(val label: String, val direction: Direction)

    enum class Direction {
        Up,
        Down,
        Unchanged,
    }
}
