package com.patrykandpatryk.liftapp.domain.navigation

sealed interface NavigationCommand {
    data class PopBackStack(
        val route: Any? = null,
        val inclusive: Boolean = true,
        val saveState: Boolean = false,
    ) : NavigationCommand

    data class Route(
        val route: Any,
        val popUpTo: Any? = null,
        val launchSingleTop: Boolean = false,
    ) : NavigationCommand

    companion object
}
