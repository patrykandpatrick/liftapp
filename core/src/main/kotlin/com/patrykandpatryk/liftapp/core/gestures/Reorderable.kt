package com.patrykandpatryk.liftapp.core.gestures

import androidx.compose.animation.core.animate
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import kotlin.math.roundToInt

@Composable
fun rememberItemRanges(key: Any? = null): ArrayList<IntRange> =
    remember(key) { ArrayList() }

fun Modifier.onItemYRange(onYRange: (IntRange) -> Unit): Modifier =
    onGloballyPositioned { layoutCoordinates ->
        val yPosition = layoutCoordinates.positionInParent().y.roundToInt()
        onYRange(yPosition..yPosition + layoutCoordinates.size.height)
    }

@Suppress("LongParameterList")
fun Modifier.reorderable(
    itemIndex: Int,
    itemYOffset: Float,
    itemRanges: List<IntRange>,
    onIsDragging: (isDragging: Boolean) -> Unit,
    onDelta: (delta: Float) -> Unit,
    onItemReordered: (from: Int, to: Int) -> Unit,
): Modifier = composed {

    var hasBeenReordered = remember(itemIndex) { false }

    draggable(
        state = rememberDraggableState { delta ->
            onDelta(delta)

            val itemRange = itemRanges[itemIndex]

            val newIndex = when {
                itemYOffset < 0f -> itemRanges.indexOfFirst { range ->
                    range.last < itemRange.first - itemYOffset && range.first > itemRange.first
                }

                itemYOffset > 0f -> itemRanges.indexOfLast { range ->
                    range.first > itemRange.last - itemYOffset && range.last < itemRange.last
                }

                else -> -1
            }

            if (hasBeenReordered.not() && newIndex != -1 && newIndex != itemIndex) {
                onItemReordered(itemIndex, newIndex)
                hasBeenReordered = true
            }
        },
        orientation = Orientation.Vertical,
        onDragStarted = {
            onIsDragging(true)
        },
        onDragStopped = { velocity ->

            var remainingScroll = itemYOffset

            animate(
                initialValue = itemYOffset,
                targetValue = 0f,
                initialVelocity = velocity,
            ) { value, _ ->
                onDelta(value - remainingScroll)
                remainingScroll = value
            }

            onIsDragging(false)
        },
    )
}
