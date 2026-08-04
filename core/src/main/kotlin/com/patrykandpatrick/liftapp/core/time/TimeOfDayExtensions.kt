package com.patrykandpatrick.liftapp.core.time

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.domain.time.TimeOfDay

val TimeOfDay.text: String
    @Composable
    @ReadOnlyComposable
    get() =
        stringResource(
            id =
                when (this) {
                    TimeOfDay.AM -> R.string.picker_time_am
                    TimeOfDay.PM -> R.string.picker_time_pm
                }
        )
