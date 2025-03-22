package com.patrykandpatryk.liftapp.feature.more.navigation

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.core.R
import kotlinx.collections.immutable.persistentListOf

@Stable
val destinations =
    persistentListOf(
        Destination(
            getRoute = { Routes.OneRepMax },
            titleResourceId = R.string.route_one_rep_max,
            iconResourceId = R.drawable.ic_calculator,
        ),
        Destination(
            getRoute = { Routes.Routine.list() },
            titleResourceId = R.string.route_routines,
            iconResourceId = R.drawable.ic_routines_outlined,
        ),
        Destination(
            getRoute = { Routes.Settings },
            titleResourceId = R.string.route_settings,
            iconResourceId = R.drawable.ic_settings,
        ),
        Destination(
            getRoute = { Routes.About },
            titleResourceId = R.string.route_about,
            iconResourceId = R.drawable.ic_info,
        ),
    )
