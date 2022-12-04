package com.patrykandpatryk.liftapp.core.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

class NavHostNavigator(private val navHostController: NavHostController) : Navigator {

    override val currentBackStackEntry: NavBackStackEntry?
        get() = navHostController.currentBackStackEntry

    override val previousBackStackEntry: NavBackStackEntry?
        get() = navHostController.previousBackStackEntry

    override val backQueue: ArrayDeque<NavBackStackEntry>
        get() = navHostController.backQueue

    override fun navigate(route: String) {
        navHostController.navigate(route = route)
    }

    override fun popBackStack() {
        navHostController.popBackStack()
    }

    override fun popBackStack(route: String, inclusive: Boolean, saveState: Boolean) {
        navHostController.popBackStack(route, inclusive, saveState)
    }
}
