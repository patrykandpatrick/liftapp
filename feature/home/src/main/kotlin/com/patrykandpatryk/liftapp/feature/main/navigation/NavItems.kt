package com.patrykandpatryk.liftapp.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.navigation.NavItemRoute
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.feature.dashboard.ui.Dashboard
import com.patrykandpatryk.liftapp.feature.about.ui.More
import com.patrykandpatryk.liftapp.feature.body.ui.Body
import com.patrykandpatryk.liftapp.feature.exercise.ui.Exercises

@Stable
val navBarRoutes: List<NavItemRoute>
    @Composable get() = remember {
        listOf(
            NavItemRoute(
                route = Routes.Home.Dashboard.value,
                titleRes = R.string.route_dashboard,
                iconRes = R.drawable.ic_dashboard,
                content = { _, modifier, _, _ ->
                    Dashboard(modifier = modifier)
                },
            ),
            NavItemRoute(
                route = Routes.Home.Exercises.value,
                titleRes = R.string.route_exercises,
                iconRes = R.drawable.ic_more_horizontal,
                content = { _, modifier, _, _ ->
                    Exercises(modifier = modifier)
                },
            ),
            NavItemRoute(
                route = Routes.Home.Body.value,
                titleRes = R.string.route_body,
                iconRes = R.drawable.ic_more_horizontal,
                content = { _, modifier, _, _ ->
                    Body(modifier = modifier)
                },
            ),
            NavItemRoute(
                route = Routes.Home.More.value,
                titleRes = R.string.route_more,
                iconRes = R.drawable.ic_more_horizontal,
                content = { _, modifier, _, navigate ->
                    More(
                        modifier = modifier,
                        navigate = navigate,
                    )
                },
            ),
        )
    }
