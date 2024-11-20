package com.patrykandpatryk.liftapp.core.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.R.string
import com.patrykandpatryk.liftapp.domain.goal.Goal

@Composable
fun Goal.getPrettyStringShort(): String =
    stringResource(string.goal_format_short, minReps, maxReps, sets)

@Composable
fun Goal.getPrettyStringLong(): String =
    if (minReps == maxReps) {
        stringResource(
            string.goal_format_long_single_rep,
            maxReps,
            pluralStringResource(R.plurals.rep_count, maxReps),
            sets,
            pluralStringResource(R.plurals.set_count, sets),
        )
    } else {
        stringResource(
            string.goal_format_long,
            minReps,
            maxReps,
            pluralStringResource(R.plurals.rep_count, maxReps),
            sets,
            pluralStringResource(R.plurals.set_count, sets),
        )
    }
