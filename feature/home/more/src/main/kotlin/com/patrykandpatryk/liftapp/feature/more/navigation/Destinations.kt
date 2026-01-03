package com.patrykandpatryk.liftapp.feature.more.navigation

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.icons.Calculator
import com.patrykandpatrick.liftapp.ui.icons.Info
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plan
import com.patrykandpatrick.liftapp.ui.icons.Routine
import com.patrykandpatrick.liftapp.ui.icons.Settings
import com.patrykandpatryk.liftapp.core.R
import kotlinx.collections.immutable.persistentListOf

@Stable
val destinations =
    persistentListOf(
        Destination(
            getRoute = { Routes.OneRepMax },
            titleResourceId = R.string.route_one_rep_max,
            imageVector = LiftAppIcons.Calculator,
        ),
        Destination(
            getRoute = { Routes.Routine.list() },
            titleResourceId = R.string.route_routines,
            imageVector = LiftAppIcons.Routine,
        ),
        Destination(
            getRoute = { Routes.Plan.list() },
            titleResourceId = R.string.route_training_plans,
            imageVector = LiftAppIcons.Plan,
        ),
        Destination(
            getRoute = { Routes.Settings },
            titleResourceId = R.string.route_settings,
            imageVector = LiftAppIcons.Settings,
        ),
        Destination(
            getRoute = { Routes.About },
            titleResourceId = R.string.route_about,
            imageVector = LiftAppIcons.Info,
        ),
    )
