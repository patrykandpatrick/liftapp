package com.patrykandpatryk.liftapp.core.navigation

import androidx.navigation.NavController

class ComposeNavigationResultListener(private val navController: NavController) : NavigationResultListener {
    override suspend fun <T> registerResultListener(key: String, listener: (result: T) -> Unit) {
        val currentBackStackEntry = requireNotNull(navController.currentBackStackEntry)
        val savedStateHandle = currentBackStackEntry.savedStateHandle

        currentBackStackEntry
            .savedStateHandle
            .getStateFlow<T?>(key, null)
            .collect { result ->
                if (result != null) {
                    listener(result)
                    savedStateHandle[key] = null
                }
            }
    }
}
