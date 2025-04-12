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

    object Format {
        const val DECIMAL_PATTERN = "0.##"
        const val INTEGER_PATTERN = "0"
    }

    object Input {
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
            private const val WEIGHT_SHORT = .25
            private const val WEIGHT_LONG = 1.0
            private const val REPS_SHORT = 1
            private const val REPS_LONG = 5
            private const val DISTANCE_SHORT = 0.25
            private const val DISTANCE_LONG = 1.0
            private const val CALORIES_SHORT = 25.0
            private const val CALORIES_LONG = 100.0

            fun getBodyWeight(long: Boolean) = if (long) BODY_WEIGHT_LONG else BODY_WEIGHT_SHORT

            fun getWeight(long: Boolean) = if (long) WEIGHT_LONG else WEIGHT_SHORT

            fun getReps(long: Boolean) = if (long) REPS_LONG else REPS_SHORT

            fun getDistance(long: Boolean) = if (long) DISTANCE_LONG else DISTANCE_SHORT

            fun getCalories(long: Boolean) = if (long) CALORIES_LONG else CALORIES_SHORT
        }
    }

    object Workout {
        const val EXERCISE_CHANGE_DELAY = 500L
    }

    object Keys {
        const val PICKED_EXERCISE_IDS = "pickedExerciseIds"
    }

    object TrainingPlan {
        const val DEFAULT_CYCLE_COUNT = 6
    }

    object Algorithms {
        const val SCREEN_STATE_KEY = "screenState"
        const val SHA1_NAME = "SHA-1"
    }
}
