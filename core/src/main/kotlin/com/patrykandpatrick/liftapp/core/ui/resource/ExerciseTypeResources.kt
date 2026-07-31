package com.patrykandpatrick.liftapp.core.ui.resource

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.ui.icons.BicepsFlexed
import com.patrykandpatrick.liftapp.ui.icons.Cardio
import com.patrykandpatrick.liftapp.ui.icons.Dumbbell
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Repeat
import com.patrykandpatrick.liftapp.ui.icons.Timer

val ExerciseType.icon: ImageVector
    get() =
        when (this) {
            ExerciseType.Weight -> LiftAppIcons.Dumbbell
            ExerciseType.Calisthenics -> LiftAppIcons.BicepsFlexed
            ExerciseType.Reps -> LiftAppIcons.Repeat
            ExerciseType.Cardio -> LiftAppIcons.Cardio
            ExerciseType.Time -> LiftAppIcons.Timer
        }

val ExerciseType.nameRes: Int
    @StringRes
    get() =
        when (this) {
            ExerciseType.Weight -> R.string.exercise_type_weight
            ExerciseType.Calisthenics -> R.string.exercise_type_calisthenics
            ExerciseType.Reps -> R.string.exercise_type_reps
            ExerciseType.Cardio -> R.string.exercise_type_cardio
            ExerciseType.Time -> R.string.exercise_type_time
        }

val ExerciseType.prettyName: String
    @Composable get() = stringResource(id = nameRes)
