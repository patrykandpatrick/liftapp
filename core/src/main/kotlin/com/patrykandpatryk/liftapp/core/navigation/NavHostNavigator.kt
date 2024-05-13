package com.patrykandpatryk.liftapp.core.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.get

typealias PlatformNavigator = androidx.navigation.Navigator<out NavDestination>

class NavHostNavigator(private val navHostController: NavHostController) : Navigator {

    private var composeNavigator: ComposeNavigator? = null

    override val currentBackStackEntry: NavBackStackEntry?
        get() = navHostController.currentBackStackEntry

    override val previousBackStackEntry: NavBackStackEntry?
        get() = navHostController.previousBackStackEntry

    override fun navigate(route: String) {
        navHostController.navigate(route = route)
    }

    override fun popBackStack() {
        if (getBackStackSize() > 1) {
            navHostController.popBackStack()
        }
    }

    override fun popBackStack(route: String, inclusive: Boolean, saveState: Boolean) {
        if (getBackStackSize() > 1) {
            navHostController.popBackStack(route, inclusive, saveState)
        }
    }

    override fun getBackStackEntry(route: String): NavBackStackEntry = navHostController.getBackStackEntry(route)

    private fun getBackStackSize(): Int = getComposeNavigator()?.backStack?.value?.size ?: 0

    private fun getComposeNavigator(): ComposeNavigator? =
        composeNavigator ?: (
                navHostController.navigatorProvider
                    .get<PlatformNavigator>("composable") as? ComposeNavigator
                )
            .also { composeNavigator = it }
}
