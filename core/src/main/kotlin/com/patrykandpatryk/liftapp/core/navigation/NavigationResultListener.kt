package com.patrykandpatryk.liftapp.core.navigation

import androidx.compose.runtime.Composable

interface NavigationResultListener {
    @Composable
    fun <T> RegisterResultListener(key: String, listener: (result: T) -> Unit)
}
