package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.animation.core.spring
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class BackdropValue {
    Opened,
    Closed,
}

@Immutable
data class BackdropState(
    val initialState: BackdropValue = BackdropValue.Closed,
    private val density: Density,
) {
    internal val anchoredDraggableState = AnchoredDraggableState<BackdropValue>(
        initialValue = BackdropValue.Closed,
        positionalThreshold = { distance -> distance / 2 },
        velocityThreshold = { with(density) { 200.dp.toPx() } },
        snapAnimationSpec = spring(),
        decayAnimationSpec = splineBasedDecay(density),
    )

    val nestedScrollConnection: NestedScrollConnection = BackdropNestedScrollConnection(anchoredDraggableState)

    val isOpened: Boolean by derivedStateOf { anchoredDraggableState.currentValue == BackdropValue.Opened }

    val offset: Float by derivedStateOf { anchoredDraggableState.offset - anchoredDraggableState.anchors.minAnchor() }

    val offsetFraction: Float by derivedStateOf {
        offset / (anchoredDraggableState.anchors.maxAnchor() - anchoredDraggableState.anchors.minAnchor())
    }
}

@Composable
fun rememberBackdropState(
    initialState: BackdropValue = BackdropValue.Closed,
): BackdropState {
    val density = LocalDensity.current
    return remember(initialState, density) { BackdropState(initialState, density) }
}

@Composable
fun Backdrop(
    backContent: @Composable () -> Unit,
    backPeekHeight: Dp,
    contentPeekHeight: Dp,
    modifier: Modifier = Modifier,
    state: BackdropState = rememberBackdropState(),
    content: @Composable () -> Unit,
) {
    Layout(
        content = {
            Box { backContent() }
            Box { content() }
        },
        measurePolicy = { measurables, constraints ->
            val backdrop = measurables[0].measure(constraints)
            val frontMaxHeight = constraints.maxHeight - backPeekHeight.roundToPx()
            val front = measurables[1].measure(
                constraints
                    .copy(
                        minHeight = constraints.minHeight.coerceAtMost(frontMaxHeight),
                        maxHeight = frontMaxHeight,
                    )
            )
            val backdropOpenLength = minOf(
                constraints.maxHeight - contentPeekHeight.toPx(),
                backdrop.height.toFloat(),
            )

            state.anchoredDraggableState.updateAnchors(
                DraggableAnchors {
                    BackdropValue.Closed at backPeekHeight.toPx()
                    BackdropValue.Opened at backdropOpenLength
                },
            )
            layout(constraints.maxWidth, constraints.maxHeight) {
                backdrop.place(0, 0)
                front.place(0, state.anchoredDraggableState.offset.roundToInt())
            }
        },
        modifier = modifier
            .anchoredDraggable(state.anchoredDraggableState, Orientation.Vertical),
    )
}

private class BackdropNestedScrollConnection(
    private val anchoredDraggableState: AnchoredDraggableState<BackdropValue>,
) : NestedScrollConnection {
    var blockDrag: Boolean? = null
    var blockScroll: Boolean? = null

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset =
        if (blockScroll == true ||
            available.y < 0 && anchoredDraggableState.offset != anchoredDraggableState.anchors.minAnchor()
        ) {
            blockScroll = true
            anchoredDraggableState.dispatchRawDelta(available.y)
            available
        } else {
            Offset.Zero
        }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (blockDrag == null) blockDrag = available.y == 0f
        return when {
            available.y > 0 -> {
                val consumedY = if (blockDrag == true) {
                    0f
                } else {
                    anchoredDraggableState.dispatchRawDelta(available.y)
                }
                Offset(available.x, consumedY)
            }

            blockScroll == true -> Offset(0f, available.y)
            else -> Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity =
        if (available.y < 0 && anchoredDraggableState.offset != anchoredDraggableState.anchors.minAnchor()) {
            anchoredDraggableState.settle(available.y / 4)
            Velocity(0f, available.y)
        } else {
            val consumedVelocityY = when {
                available.y > 0f && blockDrag == true -> 0f
                blockScroll == true -> available.y
                else -> anchoredDraggableState.settle(available.y)
            }
            Velocity(0f, consumedVelocityY)
        }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity
    ): Velocity {
        blockDrag = null
        blockScroll = null
        return Velocity(0f, anchoredDraggableState.settle(available.y))
    }
}
