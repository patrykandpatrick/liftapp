package com.patrykandpatryk.liftapp.core.ui.resource

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType

val ExerciseType.iconRes: Int
    @DrawableRes
    get() =
        when (this) {
            ExerciseType.Weight -> R.drawable.ic_workout
            ExerciseType.Calisthenics -> R.drawable.ic_calisthenics
            ExerciseType.Reps -> R.drawable.ic_reps
            ExerciseType.Cardio -> R.drawable.ic_cardio
            ExerciseType.Time -> R.drawable.ic_stopwatch
        }

val ExerciseType.iconPainter: Painter
    @Composable get() = painterResource(id = iconRes)

val ExerciseType.nameRes: Int
    @StringRes
    get() =
        when (this) {
            ExerciseType.Weight -> R.string.exercise_type_weight
            ExerciseType.Calisthenics -> R.string.exercise_type_calisthenics
            ExerciseType.Reps -> R.string.exercise_type_reps
            ExerciseType.Cardio -> R.string.exercise_type_cardio
            ExerciseType.Time -> R.string.exercise_type_calisthenics
        }

val ExerciseType.prettyName: String
    @Composable get() = stringResource(id = nameRes)
