package com.patrykandpatryk.liftapp.core.navigation

import androidx.navigation.NavBackStackEntry

interface Navigator {

    val currentBackStackEntry: NavBackStackEntry?

    val previousBackStackEntry: NavBackStackEntry?

    fun navigate(route: String)

    fun popBackStack()

    fun popBackStack(
        route: String,
        inclusive: Boolean,
        saveState: Boolean = false,
    )

    fun getBackStackEntry(route: String): NavBackStackEntry
}
