package com.patrykandpatryk.liftapp.feature.more.navigation

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.core.R
import kotlinx.collections.immutable.persistentListOf

@Stable
val destinations =
    persistentListOf(
        Destination(
            navigate = { oneRepMaxCalculator() },
            titleResourceId = R.string.route_one_rep_max,
            iconResourceId = R.drawable.ic_calculator,
        ),
        Destination(
            navigate = { settings() },
            titleResourceId = R.string.route_settings,
            iconResourceId = R.drawable.ic_settings,
        ),
        Destination(
            navigate = { about() },
            titleResourceId = R.string.route_about,
            iconResourceId = R.drawable.ic_info,
        ),
    )
