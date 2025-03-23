package com.patrykandpatryk.liftapp.navigation

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import com.patrykandpatrick.liftapp.plan.ui.PlanScreen
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.navigation.NavItemRoute
import com.patrykandpatryk.liftapp.feature.bodymeasurementlist.ui.BodyMeasurementListScreen
import com.patrykandpatryk.liftapp.feature.dashboard.ui.DashboardScreen
import com.patrykandpatryk.liftapp.feature.exercises.ui.ExerciseListScreen
import com.patrykandpatryk.liftapp.feature.more.ui.MoreScreen

@Stable
val navigationBarItems: List<NavItemRoute<Any>> by lazy {
    listOf(
        NavItemRoute(
            route = Routes.Home.Dashboard,
            titleRes = R.string.route_dashboard,
            deselectedIconRes = R.drawable.ic_dashboard,
            selectedIconRes = R.drawable.ic_dashboard_filled,
            content = { DashboardScreen(modifier = it) },
        ),
        NavItemRoute(
            route = Routes.Home.Plan,
            titleRes = R.string.route_active_plan_short,
            deselectedIconRes = R.drawable.ic_routines_outlined,
            selectedIconRes = R.drawable.ic_routines_filled,
            content = { PlanScreen(modifier = it) },
        ),
        NavItemRoute(
            route = Routes.Home.Exercises,
            titleRes = R.string.route_exercises,
            deselectedIconRes = R.drawable.ic_weightlifter_down,
            selectedIconRes = R.drawable.ic_weightlifter_up,
            content = { ExerciseListScreen(modifier = it) },
            typeMap = ExerciseListRouteData.typeMap,
        ),
        NavItemRoute(
            route = Routes.Home.BodyMeasurements,
            titleRes = R.string.route_body,
            deselectedIconRes = R.drawable.ic_weightscale_outline,
            selectedIconRes = R.drawable.ic_weightscale_filled,
            content = { BodyMeasurementListScreen(modifier = it) },
        ),
        NavItemRoute(
            route = Routes.Home.More,
            titleRes = R.string.route_more,
            deselectedIconRes = R.drawable.ic_more_horizontal,
            content = { MoreScreen(modifier = it) },
        ),
    )
}
