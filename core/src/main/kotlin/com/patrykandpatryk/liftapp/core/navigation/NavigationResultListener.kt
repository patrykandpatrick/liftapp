package com.patrykandpatryk.liftapp.core.navigation

interface NavigationResultListener {
    suspend fun <T> registerResultListener(key: String, listener: (result: T) -> Unit)
}
