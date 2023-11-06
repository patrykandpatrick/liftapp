package com.patrykandpatryk.liftapp.core.navigation

import androidx.navigation.NavBackStackEntry

object StubNavigator : Navigator {

    private const val ERR_MESSAGE = "Stub navigator!"

    override val currentBackStackEntry: NavBackStackEntry
        get() = error(ERR_MESSAGE)

    override val previousBackStackEntry: NavBackStackEntry
        get() = error(ERR_MESSAGE)

    override fun navigate(route: String) {
        error(ERR_MESSAGE)
    }

    override fun popBackStack() {
        error(ERR_MESSAGE)
    }

    override fun popBackStack(route: String, inclusive: Boolean, saveState: Boolean) {
        error(ERR_MESSAGE)
    }

    override fun getBackStackEntry(route: String): NavBackStackEntry {
        error(ERR_MESSAGE)
    }
}
