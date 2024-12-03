package com.patrykandpatryk.liftapp.domain

object Constants {
    object Logging {
        const val DISPLAYABLE_ERROR = "displayable_error"
    }

    object Database {
        const val Name = "LiftApp.db"
        const val DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ"
        const val ID_NOT_SET = 0L
    }

    object Input {
        const val INCREMENT_SHORT = 0.1f
        const val INCREMENT_LONG = 0.5f

        const val BODY_MIN_VALUE = .1
        const val BODY_MAX_WEIGHT_METRIC = 315.0
        const val BODY_MAX_WEIGHT_IMPERIAL = 700.0
        const val BODY_MAX_LENGTH_METRIC = 200.0
        const val BODY_MAX_LENGTH_IMPERIAL = 80.0
        const val BODY_MAX_PERCENTAGE = 100.0

        const val NAME_MAX_CHARS = 60
        const val TYPING_DEBOUNCE_MILLIS = 500L
        const val CLEAR_ERROR_DELAY = 4000L

        object Increment {
            private const val BODY_WEIGHT_SHORT = .1
            private const val BODY_WEIGHT_LONG = .5

            fun getBodyWeight(long: Boolean) = if (long) BODY_WEIGHT_LONG else BODY_WEIGHT_SHORT
    }

    object Keys {
        const val PICKED_EXERCISE_IDS = "pickedExerciseIds"
    }

    object Algorithms {
        const val SCREEN_STATE_KEY = "screenState"
        const val SHA1_NAME = "SHA-1"
    }
}
