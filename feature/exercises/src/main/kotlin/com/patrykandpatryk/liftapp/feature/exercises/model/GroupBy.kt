package com.patrykandpatryk.liftapp.feature.exercises.model

import androidx.annotation.StringRes
import com.patrykandpatryk.liftapp.core.R

enum class GroupBy(@StringRes val labelResourceId: Int) {
    Name(labelResourceId = R.string.generic_name),
    ExerciseType(labelResourceId = R.string.generic_type),
    MainMuscles(labelResourceId = R.string.generic_main_muscles),
}
