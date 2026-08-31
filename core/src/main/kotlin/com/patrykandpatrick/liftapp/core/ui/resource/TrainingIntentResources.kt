package com.patrykandpatrick.liftapp.core.ui.resource

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.domain.target.TrainingIntent

val TrainingIntent.stringRes: Int
    get() =
        when (this) {
            TrainingIntent.Strength -> R.string.training_intent_strength
            TrainingIntent.Hypertrophy -> R.string.training_intent_hypertrophy
            TrainingIntent.Both -> R.string.training_intent_both
        }

val TrainingIntent.prettyName: String
    @Composable get() = stringResource(stringRes)
