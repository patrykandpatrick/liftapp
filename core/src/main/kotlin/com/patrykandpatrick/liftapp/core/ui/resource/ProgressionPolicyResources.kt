package com.patrykandpatrick.liftapp.core.ui.resource

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.domain.exercise.ProgressionPolicy

val ProgressionPolicy.nameRes: Int
    @StringRes
    get() =
        when (this) {
            ProgressionPolicy.DoubleProgression -> R.string.progression_policy_double_progression
            ProgressionPolicy.FixedIncrement -> R.string.progression_policy_fixed_increment
            ProgressionPolicy.RepsOnly -> R.string.progression_policy_reps_only
        }

val ProgressionPolicy.prettyName: String
    @Composable get() = stringResource(id = nameRes)
