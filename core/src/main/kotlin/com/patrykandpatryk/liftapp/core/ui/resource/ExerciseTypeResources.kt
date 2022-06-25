package com.patrykandpatryk.liftapp.core.ui.resource

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType

val ExerciseType.iconRes: Int
    @DrawableRes
    get() = when (this) {
        ExerciseType.Weight -> R.drawable.ic_workout
        ExerciseType.Calisthenics -> R.drawable.ic_calisthenics
        ExerciseType.Reps -> R.drawable.ic_reps
        ExerciseType.Cardio -> R.drawable.ic_cardio
        ExerciseType.Time -> R.drawable.ic_stopwatch
    }

val ExerciseType.iconPainter: Painter
    @Composable
    get() = painterResource(id = iconRes)
