package com.patrykandpatrick.liftapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.navOptions
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommand

/** Navigate between bottom-bar graphs while retaining one saved back stack per tab. */
fun NavController.navigateToHomeTab(tabRoute: Routes.HomeTabRoute) {
    if (!hasDashboardEntry()) {
        // A deep link or an inclusive pop may have removed the Home graph's start destination.
        // Recreate the graph from the root so popUpTo below cannot silently fail and stack tabs.
        navigate(Routes.Home, navOptions { popUpTo(graph.id) })
    }
    navigate(
        tabRoute,
        navOptions {
            launchSingleTop = true
            restoreState = true
            popUpTo<Routes.Home.Dashboard> { saveState = true }
        },
    )
}

/** Returns to Week when system back is pressed from another top-level home tab. */
internal fun NavController.navigateBackToDefaultHomeTab(): Boolean {
    if (currentDestination?.isNonDefaultHomeTab != true) return false

    navigateToHomeTab(Routes.HomeTab.Dashboard)
    return true
}

internal val NavDestination.isNonDefaultHomeTab: Boolean
    get() = hierarchy.any {
        it.hasRoute<Routes.HomeTab.Plan>() ||
            it.hasRoute<Routes.HomeTab.Exercises>() ||
            it.hasRoute<Routes.HomeTab.BodyMeasurements>() ||
            it.hasRoute<Routes.HomeTab.More>()
    }

private fun NavController.hasDashboardEntry(): Boolean = runCatching {
    getBackStackEntry(Routes.Home.Dashboard)
}
    .isSuccess

/**
 * Executes a navigation command, redirecting bottom-bar destinations through their tab graphs. This
 * keeps dashboard shortcuts and bottom-bar taps on the same navigation path, regardless of the
 * options attached to the original command.
 */
fun NavController.navigateTo(command: NavigationCommand.Route) {
    val homeTabRoute = homeTabRouteFor(command.route)
    if (homeTabRoute != null) {
        navigateToHomeTab(homeTabRoute)
        return
    }

    navigate(
        route = command.route,
        navOptions =
            navOptions {
                command.popUpTo?.also {
                    popUpTo(it)
                    launchSingleTop = command.launchSingleTop
                }
            },
    )
}

/** Only the singleton tab destinations are redirected; picker route instances must fall through. */
internal fun homeTabRouteFor(route: Any): Routes.HomeTabRoute? =
    when {
        route === Routes.Home.Dashboard -> Routes.HomeTab.Dashboard
        route === Routes.Home.Plan -> Routes.HomeTab.Plan
        route === Routes.Home.Exercises -> Routes.HomeTab.Exercises
        route === Routes.Home.BodyMeasurements -> Routes.HomeTab.BodyMeasurements
        route === Routes.Home.More -> Routes.HomeTab.More
        else -> null
    }
