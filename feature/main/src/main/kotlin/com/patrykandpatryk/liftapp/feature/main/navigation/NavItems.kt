package com.patrykandpatryk.liftapp.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.navigation.NavItemRoute
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.feature.about.ui.About
import com.patrykandpatryk.liftapp.feature.settings.ui.Settings

@Stable
val navBarRoutes: List<NavItemRoute>
    @Composable get() = remember {
        listOf(
            NavItemRoute(
                route = Routes.Menu.Settings.value,
                titleRes = R.string.menu_route_settings,
                iconRes = R.drawable.ic_settings,
                content = { _, modifier, _, _ ->
                    Settings(modifier)
                },
            ),
            NavItemRoute(
                route = Routes.Menu.About.value,
                titleRes = R.string.menu_route_about,
                iconRes = R.drawable.ic_info,
                content = { _, modifier, _, _ ->
                    About(modifier)
                },
            ),
        )
    }
