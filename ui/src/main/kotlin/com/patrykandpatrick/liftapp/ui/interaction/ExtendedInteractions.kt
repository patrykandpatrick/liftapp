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

    data class Exit(val position: Offset) : HoverInteraction
}

fun Modifier.extendedInteractions(
    interactionSource: MutableInteractionSource,
    coroutineScope: CoroutineScope,
    key: Any = Unit,
) =
    pointerInput(key) {
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
                        when (event.type) {
                            PointerEventType.Press -> {
                                val pressInteraction = PressInteraction.Press(position)
                                lastPressInteraction = pressInteraction
                                delay(viewConfiguration.doubleTapMinTimeMillis)
                                interactionSource.emit(pressInteraction)
                            }
                            PointerEventType.Move -> {
                                if (change.pressed && lastPressInteraction != null) {
                                    if (change.isConsumed) {
                                        interactionSource.emit(
                                            PressInteraction.Cancel(lastPressInteraction!!)
                                        )
                                        lastPressInteraction = null
                                    } else {
                                        delay(viewConfiguration.doubleTapMinTimeMillis)
                                        interactionSource.emit(PressInteraction.Press(position))
                                    }
                                } else if (!change.pressed) {
                                    hadHoverInteraction = true
                                    interactionSource.emit(HoverInteraction.Enter(position))
                                }
                            }
                            PointerEventType.Release -> {
                                lastPressInteraction?.also {
                                    interactionSource.emit(PressInteraction.Release(it))
                                }
                                lastPressInteraction = null
                                if (
                                    hadHoverInteraction &&
                                        !change.isOutOfBounds(size, extendedTouchPadding)
                                ) {
                                    interactionSource.emit(HoverInteraction.Enter(position))
                                } else {
                                    hadHoverInteraction = false
                                }
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
