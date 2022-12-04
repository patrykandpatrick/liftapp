package com.patrykandpatryk.liftapp.core.provider

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import androidx.navigation.NavHostController
import com.patrykandpatryk.liftapp.core.navigation.NavHostNavigator
import com.patrykandpatryk.liftapp.core.navigation.Navigator
import com.patrykandpatryk.liftapp.core.navigation.StubNavigator

val LocalNavigator: ProvidableCompositionLocal<Navigator> = compositionLocalOf { StubNavigator }

val navigator: Navigator
    @Composable get() = LocalNavigator.current

fun interface ResultListenerScope {

    fun clearResult()
}

@Composable
fun ProvideNavHost(navHostController: NavHostController, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNavigator provides NavHostNavigator(navHostController), content = content)
}

fun <T> Navigator.setResult(
    key: String,
    result: T,
    route: String = requireNotNull(previousBackStackEntry?.destination?.route),
) {
    val entry = backQueue.first { it.destination.route == route }
    entry.savedStateHandle[key] = result
}

@Composable
fun <T> Navigator.RegisterResultListener(
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
    LocalNavigator.current.RegisterResultListener(key, onResult)
}

fun Modifier.onClickNavigate(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onGetRoute: () -> String,
) = composed {

    val navHostController = LocalNavigator.current

    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = { navHostController.navigate(onGetRoute()) },
    )
}
