package com.patrykandpatryk.liftapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.NavController

class ComposeNavigationResultListener(private val navController: NavController) : NavigationResultListener {
    @Composable
    override fun <T> RegisterResultListener(key: String, listener: (result: T) -> Unit) {
        val currentBackStackEntry = requireNotNull(navController.currentBackStackEntry)
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        val listenerState = rememberUpdatedState(newValue = listener)

        LaunchedEffect(key1 = key) {
            currentBackStackEntry
                .savedStateHandle
                .getStateFlow<T?>(key, null)
                .collect { result ->
                    if (result != null) {
                        listenerState.value(result)
                        savedStateHandle[key] = null
                    }
                }
        }
    }
}
