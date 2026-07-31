package com.patrykandpatrick.liftapp.core.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter

@Composable
fun getPermissionGrantedState(permission: String): State<Boolean> =
    rememberPermissionGrantedState(permission)

/**
 * Asks the user for [permission] the first time this enters composition, and returns whether it is
 * granted. The system shows its dialog only while the user has neither granted nor permanently
 * denied the permission; past that point the request is a no-op, so this never nags.
 */
@Composable
fun requestPermission(permission: String): State<Boolean> {
    val state = rememberPermissionGrantedState(permission)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            state.value = granted
        }

    LaunchedEffect(permission) { if (!state.value) launcher.launch(permission) }
    return state
}

@Composable
private fun rememberPermissionGrantedState(permission: String): MutableState<Boolean> {
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
