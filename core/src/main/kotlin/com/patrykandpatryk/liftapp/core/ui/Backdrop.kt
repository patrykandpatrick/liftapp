package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import kotlin.math.roundToInt

enum class BackdropValue {
    Open,
    Closed,
}

@Immutable
data class BackdropState(val initialValue: BackdropValue) {
    internal val anchoredDraggableState = AnchoredDraggableState(initialValue)

    internal val isNotClosed: Boolean by derivedStateOf {
        anchoredDraggableState.offset !=
            anchoredDraggableState.anchors.positionOf(BackdropValue.Closed)
    }

    val offsetFraction: Float by derivedStateOf {
        (anchoredDraggableState.offset -
            anchoredDraggableState.anchors.positionOf(BackdropValue.Closed)) /
            (anchoredDraggableState.anchors.positionOf(BackdropValue.Open) -
                anchoredDraggableState.anchors.positionOf(BackdropValue.Closed))
    }
}

@Composable
fun rememberBackdropState(initialState: BackdropValue = BackdropValue.Closed) =
    remember(initialState) { BackdropState(initialState) }

@Composable
fun Backdrop(
    backContent: @Composable BoxScope.() -> Unit,
    backPeekHeight: Density.() -> Dp,
    contentPeekHeight: Density.() -> Dp,
    modifier: Modifier = Modifier,
    state: BackdropState = rememberBackdropState(),
    content: @Composable BoxScope.() -> Unit,
) {
    val flingBehavior =
        AnchoredDraggableDefaults.flingBehavior(
            state = state.anchoredDraggableState,
            animationSpec = tween(easing = LinearOutSlowInEasing),
        )
    val scrollConnection =
        remember(state, flingBehavior) { BackdropNestedScrollConnection(state, flingBehavior) }
    Layout(
        content = {
            Box(content = backContent)
            Box(Modifier.nestedScroll(scrollConnection).pointerInput(Unit) {}, content = content)
        },
        measurePolicy = { measurables, constraints ->
            val backdrop = measurables[0].measure(constraints)
            val backPeekHeight = backPeekHeight().roundToPx()
            val frontMaxHeight = constraints.maxHeight - backPeekHeight
            val front =
                measurables[1].measure(
                    constraints.copy(
                        minHeight = constraints.minHeight.coerceAtMost(frontMaxHeight),
                        maxHeight = frontMaxHeight,
                    )
                )
            val backdropOpenLength =
                minOf(constraints.maxHeight - contentPeekHeight().toPx(), backdrop.height.toFloat())

            state.anchoredDraggableState.updateAnchors(
                DraggableAnchors {
                    BackdropValue.Open at backdropOpenLength
                    BackdropValue.Closed at backPeekHeight.toFloat()
                }
            )
            layout(constraints.maxWidth, constraints.maxHeight) {
                backdrop.place(0, 0)
                front.place(0, state.anchoredDraggableState.offset.roundToInt())
            }
        },
        modifier =
            modifier.anchoredDraggable(
                state = state.anchoredDraggableState,
                orientation = Orientation.Vertical,
                flingBehavior = flingBehavior,
            ),
    )
}

private class BackdropNestedScrollConnection(
    private val state: BackdropState,
    private val flingBehavior: FlingBehavior,
) : NestedScrollConnection {
    var lastUpwardScrollSource: NestedScrollSource? = null
    val scrollScope =
        object : ScrollScope {
            override fun scrollBy(pixels: Float) =
                state.anchoredDraggableState.dispatchRawDelta(pixels)
        }

    override fun onPreScroll(available: Offset, source: NestedScrollSource) =
        if (available.y < 0 && state.isNotClosed) {
            Offset(0f, state.anchoredDraggableState.dispatchRawDelta(available.y))
        } else {
            Offset.Zero
        }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        if (available.y <= 0) return Offset.Zero
        lastUpwardScrollSource = source
        return if (source == NestedScrollSource.UserInput) {
            Offset(0f, state.anchoredDraggableState.dispatchRawDelta(available.y))
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity) =
        if (available.y < 0 && state.isNotClosed) {
            state.anchoredDraggableState
            val consumed = with(flingBehavior) { scrollScope.performFling(available.y) }
            Velocity(0f, consumed)
        } else {
            available
        }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity) =
        if (
            available.y >= 0 &&
                lastUpwardScrollSource != null &&
                lastUpwardScrollSource == NestedScrollSource.UserInput
        ) {
            val consumed = with(flingBehavior) { scrollScope.performFling(available.y) }
            Velocity(0f, consumed)
        } else {
            available
        }
}
