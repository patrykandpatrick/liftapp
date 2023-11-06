package com.patrykandpatryk.liftapp.core.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

class NavHostNavigator(private val navHostController: NavHostController) : Navigator {

    override val currentBackStackEntry: NavBackStackEntry?
        get() = navHostController.currentBackStackEntry

    override val previousBackStackEntry: NavBackStackEntry?
        get() = navHostController.previousBackStackEntry

    override fun navigate(route: String) {
        navHostController.navigate(route = route)
    }

    override fun popBackStack() {
        navHostController.popBackStack()
    }

    override fun popBackStack(route: String, inclusive: Boolean, saveState: Boolean) {
        navHostController.popBackStack(route, inclusive, saveState)
    }

    override fun getBackStackEntry(route: String): NavBackStackEntry = navHostController.getBackStackEntry(route)
}
