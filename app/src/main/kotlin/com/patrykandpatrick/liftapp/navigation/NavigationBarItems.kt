package com.patrykandpatrick.liftapp.navigation

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.navigation.NavItemRoute
import com.patrykandpatrick.liftapp.feature.bodymeasurementlist.ui.BodyMeasurementListScreen
import com.patrykandpatrick.liftapp.feature.dashboard.ui.DashboardScreen
import com.patrykandpatrick.liftapp.feature.exercises.ui.ExerciseListScreen
import com.patrykandpatrick.liftapp.feature.more.ui.MoreScreen
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import com.patrykandpatrick.liftapp.plan.ui.PlanScreen
import com.patrykandpatrick.liftapp.ui.icons.BicepsFlexed
import com.patrykandpatrick.liftapp.ui.icons.House
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Menu
import com.patrykandpatrick.liftapp.ui.icons.Routine
import com.patrykandpatrick.liftapp.ui.icons.Scale

@Stable
val navigationBarItems: List<NavItemRoute<Any>> by lazy {
    listOf(
        NavItemRoute(
            route = Routes.Home.Dashboard,
            titleRes = R.string.route_dashboard,
            icon = LiftAppIcons.House,
            content = { DashboardScreen(modifier = it) },
        ),
        NavItemRoute(
            route = Routes.Home.Plan,
            titleRes = R.string.route_active_plan_short,
            icon = LiftAppIcons.Routine,
            content = { PlanScreen(modifier = it) },
        ),
        NavItemRoute(
            route = Routes.Home.Exercises,
            titleRes = R.string.route_exercises,
            icon = LiftAppIcons.BicepsFlexed,
            content = { ExerciseListScreen(modifier = it) },
            typeMap = ExerciseListRouteData.typeMap,
        ),
        NavItemRoute(
            route = Routes.Home.BodyMeasurements,
            titleRes = R.string.route_body,
            icon = LiftAppIcons.Scale,
            content = { BodyMeasurementListScreen(modifier = it) },
        ),
        NavItemRoute(
            route = Routes.Home.More,
            titleRes = R.string.route_more,
            icon = LiftAppIcons.Menu,
            content = { MoreScreen(modifier = it) },
        ),
    )
}
