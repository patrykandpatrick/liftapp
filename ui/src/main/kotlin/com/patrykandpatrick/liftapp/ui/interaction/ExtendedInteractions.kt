package com.patrykandpatrick.liftapp.ui.interaction

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed interface HoverInteraction : Interaction {
    data class Enter(val position: Offset) : HoverInteraction

    data class EnterFromRelease(val position: Offset) : HoverInteraction

    data class Exit(val position: Offset) : HoverInteraction
}

fun Modifier.extendedInteractions(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    coroutineScope: CoroutineScope,
    key: Any = Unit,
) =
    then(
        if (enabled) {
            Modifier.pointerInput(key) {
                awaitPointerEventScope {
                    var lastPressInteraction: PressInteraction.Press? = null
                    var hadHoverInteraction = false
                    var emitJob: Job? = null
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val change = event.changes.first()
                        val position = change.position
                        emitJob?.cancel()

                        emitJob =
                            coroutineScope.launch {
                                val isOutOfBounds = change.isOutOfBounds(size, extendedTouchPadding)
                                when (event.type) {
                                    PointerEventType.Press -> {
                                        val pressInteraction = PressInteraction.Press(position)
                                        lastPressInteraction = pressInteraction
                                        delay(viewConfiguration.doubleTapMinTimeMillis)
                                        interactionSource.emit(pressInteraction)
                                    }

                                    PointerEventType.Move -> {
                                        when {
                                            (isOutOfBounds || change.pressed) &&
                                                lastPressInteraction != null -> {
                                                if (change.isConsumed) {
                                                    interactionSource.emit(
                                                        PressInteraction.Cancel(
                                                            lastPressInteraction!!
                                                        )
                                                    )
                                                    lastPressInteraction = null
                                                }
                                            }
                                            isOutOfBounds -> {
                                                interactionSource.emit(
                                                    HoverInteraction.Exit(position)
                                                )
                                            }
                                            !change.pressed -> {
                                                interactionSource.emit(
                                                    HoverInteraction.Enter(position)
                                                )
                                            }
                                        }
                                        hadHoverInteraction = !change.pressed
                                    }

                                    PointerEventType.Release -> {
                                        if (hadHoverInteraction && !isOutOfBounds) {
                                            interactionSource.emit(
                                                HoverInteraction.EnterFromRelease(position)
                                            )
                                        } else {
                                            hadHoverInteraction = false
                                            lastPressInteraction?.also {
                                                interactionSource.emit(PressInteraction.Release(it))
                                            }
                                        }
                                        lastPressInteraction = null
                                    }

                                    PointerEventType.Exit -> {
                                        interactionSource.emit(HoverInteraction.Exit(position))
                                    }

                                    else -> Unit
                                }
                            }
                    }
                }
            }
        } else {
            Modifier
        }
    )
