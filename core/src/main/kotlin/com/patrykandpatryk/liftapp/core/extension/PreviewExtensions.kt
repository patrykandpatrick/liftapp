package com.patrykandpatryk.liftapp.core.extension

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.lang.reflect.Proxy

@Composable
fun ProvideLandscapeMode(content: @Composable () -> Unit) {
    val configuration = Configuration(LocalConfiguration.current)
    configuration.orientation = Configuration.ORIENTATION_LANDSCAPE
    CompositionLocalProvider(LocalConfiguration provides configuration, content = content)
}

@Composable
inline fun <reified T : Any> interfaceStub(): T {
    val classLoader = LocalContext.current.classLoader
    return Proxy.newProxyInstance(classLoader, arrayOf(T::class.java)) { _, method, _ ->
        when {
            method.name == "equals" -> true
            else -> Unit
        }
    } as T
}
