package pl.patrykgoworowski.mintlift.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import pl.patrykgoworowski.mintlift.core.R
import pl.patrykgoworowski.mintlift.core.navigation.NavItemRoute
import pl.patrykgoworowski.mintlift.core.navigation.Routes
import pl.patrykgoworowski.mintlift.feature.about.ui.About
import pl.patrykgoworowski.mintlift.feature.settings.ui.Settings

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
