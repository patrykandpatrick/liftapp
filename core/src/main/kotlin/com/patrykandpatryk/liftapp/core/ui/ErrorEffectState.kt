package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

@Stable
class ErrorEffectState {
    var scheduled by mutableStateOf(false)
        private set

    private val listeners = mutableListOf<MutableState<Boolean>>()

    fun play() {
        scheduled = true
        listeners.forEach { it.value = true }
    }

    fun stop() {
        scheduled = false
        listeners.forEach { it.value = false }
    }

    @Composable
    fun Register(
        enabled: Boolean,
        playAnimation: suspend () -> Unit,
        snapToEnd: suspend () -> Unit,
    ) {
        val scheduled = remember { mutableStateOf(this.scheduled) }

        LaunchedEffect(scheduled.value) {
            if (scheduled.value && enabled) {
                playAnimation()
                scheduled.value = false
            } else {
                snapToEnd()
            }
        }

        DisposableEffect(Unit) {
            listeners.add(scheduled)
            onDispose { listeners.remove(scheduled) }
        }
    }
}

@Composable fun rememberErrorEffectState(): ErrorEffectState = remember { ErrorEffectState() }

fun Modifier.animateJump(errorEffectState: ErrorEffectState, enabled: Boolean = true): Modifier =
    composed {
        val scale = remember { Animatable(1f) }

        errorEffectState.Register(
            enabled = enabled,
            playAnimation = {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec =
                        keyframes {
                            1.05f at 100 using EaseIn
                            .95f at 200 using EaseOut
                            1f at 300 using EaseIn
                        },
                )
            },
            snapToEnd = { scale.snapTo(1f) },
        )

        graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
    }
