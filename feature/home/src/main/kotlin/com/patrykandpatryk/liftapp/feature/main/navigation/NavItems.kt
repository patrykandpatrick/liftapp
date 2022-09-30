package com.patrykandpatryk.liftapp.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.navigation.NavItemRoute
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.feature.about.ui.More
import com.patrykandpatryk.liftapp.feature.body.ui.Body
import com.patrykandpatryk.liftapp.feature.dashboard.ui.Dashboard
import com.patrykandpatryk.liftapp.feature.exercises.ui.Exercises
import com.patrykandpatryk.liftapp.feature.routines.ui.Routines

@Stable
val navBarRoutes: List<NavItemRoute>
    @Composable get() = remember {
        listOf(
            NavItemRoute(
                route = Routes.Home.Dashboard.value,
                titleRes = R.string.route_dashboard,
                deselectedIconRes = R.drawable.ic_dashboard,
                selectedIconRes = R.drawable.ic_dashboard_filled,
                content = { _, modifier, _, _ ->
                    Dashboard(modifier = modifier)
                },
            ),
            NavItemRoute(
                route = Routes.Home.Routines.value,
                titleRes = R.string.route_routines,
                deselectedIconRes = R.drawable.ic_routines_outlined,
                selectedIconRes = R.drawable.ic_routines_filled,
                content = { _, modifier, padding, navigate ->
                    Routines(
                        modifier = modifier,
                        padding = padding,
                        navigate = navigate,
                    )
                },
            ),
            NavItemRoute(
                route = Routes.Home.Exercises.value,
                titleRes = R.string.route_exercises,
                deselectedIconRes = R.drawable.ic_weightlifter_down,
                selectedIconRes = R.drawable.ic_weightlifter_up,
                content = { _, modifier, padding, navigate ->
                    Exercises(
                        modifier = modifier,
                        padding = padding,
                        navigate = navigate,
                    )
                },
            ),
            NavItemRoute(
                route = Routes.Home.Body.value,
                titleRes = R.string.route_body,
                deselectedIconRes = R.drawable.ic_weightscale_outline,
                selectedIconRes = R.drawable.ic_weightscale_filled,
                content = { _, modifier, _, navigate ->
                    Body(
                        modifier = modifier,
                        navigate = navigate,
                    )
                },
            ),
            NavItemRoute(
                route = Routes.Home.More.value,
                titleRes = R.string.route_more,
                deselectedIconRes = R.drawable.ic_more_horizontal,
                content = { _, modifier, _, navigate ->
                    More(
                        modifier = modifier,
                        navigate = navigate,
                    )
                },
            ),
        )
    }
