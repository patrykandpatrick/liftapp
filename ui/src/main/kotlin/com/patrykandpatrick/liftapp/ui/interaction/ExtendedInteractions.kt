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

sealed interface HoverInteraction : Interaction {
    data class Enter(val position: Offset) : HoverInteraction

    data class EnterFromRelease(val position: Offset) : HoverInteraction

    data class Exit(val position: Offset) : HoverInteraction
}

fun Modifier.extendedInteractions(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    key: Any = Unit,
) =
    then(
        if (enabled) {
            Modifier.pointerInput(key) {
                awaitPointerEventScope {
                    var lastPressInteraction: PressInteraction.Press? = null
                    var hadHoverInteraction = false
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val change = event.changes.first()
                        val position = change.position
                        val isOutOfBounds = change.isOutOfBounds(size, extendedTouchPadding)
                        when (event.type) {
                            PointerEventType.Press -> {
                                lastPressInteraction?.also {
                                    interactionSource.tryEmit(PressInteraction.Cancel(it))
                                }
                                PressInteraction.Press(position).also {
                                    lastPressInteraction = it
                                    interactionSource.tryEmit(it)
                                }
                            }

                            PointerEventType.Move -> {
                                when {
                                    lastPressInteraction != null &&
                                        (isOutOfBounds || change.isConsumed) -> {
                                        lastPressInteraction?.let {
                                            interactionSource.tryEmit(PressInteraction.Cancel(it))
                                        }
                                        lastPressInteraction = null
                                    }
                                    !change.pressed && isOutOfBounds -> {
                                        interactionSource.tryEmit(HoverInteraction.Exit(position))
                                        hadHoverInteraction = false
                                    }
                                    !change.pressed -> {
                                        interactionSource.tryEmit(HoverInteraction.Enter(position))
                                        hadHoverInteraction = true
                                    }
                                }
                            }

                            PointerEventType.Release -> {
                                lastPressInteraction?.also {
                                    interactionSource.tryEmit(PressInteraction.Release(it))
                                }
                                lastPressInteraction = null

                                if (hadHoverInteraction && !isOutOfBounds) {
                                    interactionSource.tryEmit(
                                        HoverInteraction.EnterFromRelease(position)
                                    )
                                }
                            }

                            PointerEventType.Exit -> {
                                lastPressInteraction?.also {
                                    interactionSource.tryEmit(PressInteraction.Cancel(it))
                                }
                                lastPressInteraction = null
                                hadHoverInteraction = false
                                interactionSource.tryEmit(HoverInteraction.Exit(position))
                            }

                            else -> Unit
                        }
                    }
                }
            }
        } else {
            Modifier
        }
    )
