package com.patrykandpatryk.liftapp.core.time

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.time.TimeOfDay

val TimeOfDay.text: String
    @Composable get() = when (this) {
        TimeOfDay.AM -> R.string.picker_time_am
        TimeOfDay.PM -> R.string.picker_time_pm
    }.let { stringResource(id = it) }
