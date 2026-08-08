package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatrick.liftapp.core.text.IntTextFieldState
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet

/**
 * Reps in reserve is offered as a small set of whole numbers rather than a free number, because
 * lifters cannot tell the difference above about four reps from failure, and the extra precision
 * would not change anything the app does with the value.
 */
internal val rirOptions = ExerciseSet.RIR_RANGE.toList()

/**
 * The reported reps in reserve, or `null` when the user left it blank.
 *
 * [IntTextFieldState.value] cannot express this: it falls back to `0` for empty text, and `0` is a
 * set taken to failure. The raw text is the only place the difference survives.
 */
internal val IntTextFieldState.recordedRir: Int?
    get() = text.toIntOrNull()?.coerceIn(ExerciseSet.RIR_RANGE)

internal fun IntTextFieldState.updateRir(rir: Int?) {
    if (rir != null) updateValue(rir.coerceIn(ExerciseSet.RIR_RANGE)) else clearRir()
}

/** Selects [rir], or clears the field when it is already the selected option. */
internal fun IntTextFieldState.toggleRir(rir: Int) {
    updateRir(rir.takeUnless { it == recordedRir })
}

private fun IntTextFieldState.clearRir() {
    updateText("")
}
