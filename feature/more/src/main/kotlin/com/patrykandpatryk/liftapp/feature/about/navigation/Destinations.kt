package com.patrykandpatryk.liftapp.feature.about.navigation

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.R

@Stable
val destinations = listOf(
    Destination(
        route = Routes.OneRepMax.value,
        titleResourceId = R.string.route_one_rep_max,
        iconResourceId = R.drawable.ic_calculator,
    ),
    Destination(
        route = Routes.Settings.value,
        titleResourceId = R.string.route_settings,
        iconResourceId = R.drawable.ic_settings,
    ),
    Destination(
        route = Routes.About.value,
        titleResourceId = R.string.route_about,
        iconResourceId = R.drawable.ic_info,
    ),
)
