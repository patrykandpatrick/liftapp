package com.patrykandpatrick.liftapp.feature.exercises.model

import androidx.annotation.StringRes
import com.patrykandpatrick.liftapp.core.R

enum class GroupBy(@StringRes val labelResourceId: Int) {
    Name(labelResourceId = R.string.generic_name),
    ExerciseType(labelResourceId = R.string.generic_type),
    MainMuscles(labelResourceId = R.string.generic_main_muscles),

    /**
     * Doubles as the audit view for the classification: it is the only place an "Unclassified"
     * bucket appears, so a user can work through their own exercises at their own pace instead of
     * being prompted.
     */
    Role(labelResourceId = R.string.generic_role),
}
