package com.patrykandpatryk.liftapp.core.navigation

import androidx.navigation.NavBackStackEntry

interface Navigator {

    val currentBackStackEntry: NavBackStackEntry?

    val previousBackStackEntry: NavBackStackEntry?

    val currentRoute: String?
        get() = currentBackStackEntry?.destination?.route

    val canNavigateBack: Boolean
        get() = previousBackStackEntry != null

    fun navigate(route: String)

    fun popBackStack()

    fun popBackStack(
        route: String,
        inclusive: Boolean,
        saveState: Boolean = false,
    )

    fun getBackStackEntry(route: String): NavBackStackEntry
}
