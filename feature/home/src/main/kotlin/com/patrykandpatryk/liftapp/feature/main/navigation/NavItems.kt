package com.patrykandpatryk.liftapp.feature.main.navigation

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.navigation.NavItemRoute

@Stable
val navBarRoutes: List<NavItemRoute<HomeRoute>> by lazy {
    listOf(
        NavItemRoute(
            route = HomeRoute.Dashboard,
            titleRes = R.string.route_dashboard,
            deselectedIconRes = R.drawable.ic_dashboard,
            selectedIconRes = R.drawable.ic_dashboard_filled,
        ),
        NavItemRoute(
            route = HomeRoute.Plan,
            titleRes = R.string.route_active_plan_short,
            deselectedIconRes = R.drawable.ic_routines_outlined,
            selectedIconRes = R.drawable.ic_routines_filled,
        ),
        NavItemRoute(
            route = HomeRoute.Exercises,
            titleRes = R.string.route_exercises,
            deselectedIconRes = R.drawable.ic_weightlifter_down,
            selectedIconRes = R.drawable.ic_weightlifter_up,
        ),
        NavItemRoute(
            route = HomeRoute.BodyMeasurements,
            titleRes = R.string.route_body,
            deselectedIconRes = R.drawable.ic_weightscale_outline,
            selectedIconRes = R.drawable.ic_weightscale_filled,
        ),
        NavItemRoute(
            route = HomeRoute.More,
            titleRes = R.string.route_more,
            deselectedIconRes = R.drawable.ic_more_horizontal,
        ),
    )
}
