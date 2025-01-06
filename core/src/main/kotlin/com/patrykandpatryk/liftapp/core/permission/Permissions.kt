package com.patrykandpatryk.liftapp.core.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter

@Composable
fun getPermissionGrantedState(permission: String): State<Boolean> {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state = remember { mutableStateOf(context.isPermissionGranted(permission)) }

    if (!state.value) {
        LaunchedEffect(lifecycle) {
            lifecycle.currentStateFlow
                .filter { it == Lifecycle.State.RESUMED }
                .drop(1)
                .collect { state.value = context.isPermissionGranted(permission) }
        }
    }
    return state
}

private fun Context.isPermissionGranted(permission: String): Boolean =
    checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
