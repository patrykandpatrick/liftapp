package com.patrykandpatryk.liftapp.core.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController

val LocalNavHostController: ProvidableCompositionLocal<NavHostController?> = compositionLocalOf { null }

fun interface ResultListenerScope {

    fun clearResult()
}

@Composable
fun ProvideNavHost(navHostController: NavHostController, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNavHostController provides navHostController, content = content)
}

fun <T> NavHostController.setResult(
    key: String,
    result: T,
    route: String = requireNotNull(previousBackStackEntry?.destination?.route),
) {
    val entry = backQueue.first { it.destination.route == route }
    entry.savedStateHandle[key] = result
}

@Composable
fun <T> NavHostController.RegisterResultListener(
    key: String,
    onResult: ResultListenerScope.(T) -> Unit,
) {
    val savedStateHandle = requireNotNull(currentBackStackEntry).savedStateHandle
    val scope = remember(key) {
        ResultListenerScope { savedStateHandle[key] = null }
    }

    LaunchedEffect(key1 = key) {
        requireNotNull(currentBackStackEntry)
            .savedStateHandle
            .getStateFlow<T?>(key, null)
            .collect { result ->
                result?.also { scope.onResult(it) }
            }
    }
}

@Composable
fun <T> RegisterResultListener(
    key: String,
    onResult: ResultListenerScope.(T) -> Unit,
) {
    LocalNavHostController.current?.RegisterResultListener(key, onResult)
}
