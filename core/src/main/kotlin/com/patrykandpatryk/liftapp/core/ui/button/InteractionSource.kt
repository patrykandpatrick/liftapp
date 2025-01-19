package com.patrykandpatryk.liftapp.core.ui.button

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalViewConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

@SuppressLint("ComposableNaming")
@Composable
@NonRestartableComposable
fun InteractionSource.onRepeatedLongPress(repeatLongClicks: Boolean, action: () -> Unit) {
    val actionState = rememberUpdatedState(newValue = action)
    val longPressDelay = LocalViewConfiguration.current.longPressTimeoutMillis

    LaunchedEffect(key1 = this) {
        interactions
            .map { interaction -> interaction is PressInteraction.Press }
            .collectLatest { isPressed ->
                while (isPressed) {
                    delay(longPressDelay)
                    actionState.value()

                    if (repeatLongClicks.not()) break
                }
            }
    }
}

@Composable
fun InteractionSource.OnClick(action: () -> Unit) {
    val actionState = rememberUpdatedState(action)
    LaunchedEffect(this) {
        interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                actionState.value()
            }
        }
    }
}

@Composable
fun InteractionSource.OnFocusChanged(action: (isFocused: Boolean) -> Unit) {
    val actionState = rememberUpdatedState(action)
    LaunchedEffect(this) {
        interactions.collectLatest { interaction ->
            if (interaction is FocusInteraction) {
                actionState.value(interaction is FocusInteraction.Focus)
            }
        }
    }
}
