package com.patrykandpatryk.liftapp.core.ui.wheel

import android.os.Bundle
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FloatExponentialDecaySpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import kotlin.math.abs
import kotlin.math.max
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun WheelPicker(
    modifier: Modifier = Modifier,
    state: WheelPickerState = rememberWheelPickerState(),
    itemExtent: Int = 3,
    onItemSelected: (Int) -> Unit = {},
    scrollAnimationSpec: AnimationSpec<Float> =
        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow),
    highlight: @Composable () -> Unit = {},
    items: @Composable WheelPickerScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val scope = remember { WheelPickerScopeImpl() }
    val onItemSelectedState = rememberUpdatedState(onItemSelected)
    state.scrollAnimationSpec = scrollAnimationSpec

    Layout(
        contents = listOf(highlight) + { items(scope) },
        measurePolicy =
            remember(state, itemExtent) {
                WheelPickerMeasurePolicy(
                    state = state,
                    itemExtent = itemExtent,
                    onItemSelected = onItemSelectedState,
                )
            },
        modifier =
            modifier
                .clip(RectangleShape)
                .draggable(
                    state = state.draggableState,
                    orientation = Orientation.Vertical,
                    onDragStopped = { velocity -> state.snap(velocity) },
                    interactionSource = state.internalInteractionSource,
                )
                .pointerInput(state) {
                    detectTapGestures { offset ->
                        val firstVisibleIndex =
                            state.currentItem - (size.height / state.maxItemHeight) / 2
                        val clickedItemIndex = offset.y.toInt() / state.maxItemHeight
                        coroutineScope.launch {
                            state.animateScrollTo(
                                firstVisibleIndex + clickedItemIndex,
                                MutatePriority.UserInput,
                            )
                        }
                    }
                }
                .pointerInput(state) {
                    awaitEachGesture {
                        var pressInteraction: PressInteraction.Press? = null
                        while (true) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Press ->
                                    PressInteraction.Press(event.changes.first().position).also {
                                        pressInteraction = it
                                    }

                                PointerEventType.Release ->
                                    pressInteraction?.let(PressInteraction::Release)

                                else -> null
                            }?.also { interaction ->
                                coroutineScope.launch {
                                    state.internalInteractionSource.emit(interaction)
                                }
                            }
                            state.isDragInProgress = event.type == PointerEventType.Move
                        }
                    }
                },
    )
}

private class WheelPickerMeasurePolicy(
    private val state: WheelPickerState,
    private val itemExtent: Int,
    private val onItemSelected: State<(Int) -> Unit>,
) : MultiContentMeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<List<Measurable>>,
        constraints: Constraints,
    ): MeasureResult {
        val itemPlaceables = measurables[1].map { it.measure(constraints) }
        var width = itemPlaceables.maxOf { it.width }
        var maxItemHeight = itemPlaceables.maxOf { it.height }
        var visibleItemCount = (itemExtent * 2 + 1).coerceAtMost(itemPlaceables.size)
        if (visibleItemCount % 2 == 0) visibleItemCount++
        val highlightConstraints = constraints.copy(minWidth = width, minHeight = maxItemHeight)
        val highlightPlaceables = measurables[0].map { it.measure(highlightConstraints) }
        width = max(width, highlightPlaceables.maxOfOrNull { it.width } ?: 0)
        maxItemHeight = max(maxItemHeight, highlightPlaceables.maxOfOrNull { it.height } ?: 0)
        val height = visibleItemCount * maxItemHeight
        val centerLine = height / 2
        state.itemCount = itemPlaceables.size
        state.maxItemHeight = maxItemHeight

        if (!state.initialScrollCalculated) {
            state.setScroll(
                value = (height - maxItemHeight) / 2 - state.currentItem * maxItemHeight.toFloat(),
                minScroll = -itemPlaceables.size * maxItemHeight + (height + maxItemHeight) / 2f,
                maxScroll = (height - maxItemHeight) / 2f,
            )
        }

        return layout(width, height) {
            highlightPlaceables.forEach { placeable ->
                placeable.place(0, (height - placeable.height) / 2)
            }

            val firstItemIndex =
                (((state.maxScroll - state.scroll.floatValue) / maxItemHeight).toInt() -
                        visibleItemCount / 2)
                    .coerceAtLeast(0)
            val lastItemIndex =
                (firstItemIndex + visibleItemCount).coerceAtMost(itemPlaceables.lastIndex)
            for (index in firstItemIndex..lastItemIndex) {
                val placeable = itemPlaceables[index]
                val itemY = (state.scroll.floatValue + maxItemHeight * index)
                val placementY = itemY + (maxItemHeight - placeable.height) / 2
                placeable.place((width - placeable.width) / 2, placementY.toInt())
                val listPickerItemSpecNode = placeable.parentData as? ListPickerItemSpecNode
                if (
                    itemY < centerLine &&
                        itemY + maxItemHeight > centerLine &&
                        state.currentItem != index
                ) {
                    state.currentItem = index
                    onItemSelected.value(index)
                }

                if (state.currentItem == index) {
                    state.currentItemOffset =
                        ((centerLine - itemY - maxItemHeight / 2) / maxItemHeight).coerceIn(
                            -.5f,
                            .5f,
                        )
                }

                if (listPickerItemSpecNode != null) {
                    val selectedPositionOffset =
                        (itemY + maxItemHeight / 2 - centerLine) / maxItemHeight
                    val viewPortOffset = (itemY + maxItemHeight / 2 - centerLine) / (height / 2)
                    listPickerItemSpecNode.onPositionChange?.invoke(
                        selectedPositionOffset.coerceIn(-1f, 1f),
                        viewPortOffset.coerceIn(-1f, 1f),
                    )
                }
            }
        }
    }
}

class WheelPickerState(initialSelectedIndex: Int = 0) {
    var currentItem by mutableIntStateOf(initialSelectedIndex)
        internal set

    var currentItemOffset by mutableFloatStateOf(0f)
        internal set

    private var scrollJob: Job? = null

    internal var currentScrollPriority: MutatePriority? = null

    internal var isDragInProgress = false

    internal var scrollAnimationSpec: AnimationSpec<Float> = snap()

    internal var itemCount: Int = 0

    var maxItemHeight: Int by mutableIntStateOf(0)
        internal set

    internal var minScroll = 0f

    internal var maxScroll = 0f

    internal val draggableState = DraggableState(::onScrollDelta)

    internal val internalInteractionSource = MutableInteractionSource()

    val interactionSource: InteractionSource
        get() = internalInteractionSource

    val scroll = mutableFloatStateOf(0f)

    var currentScroll: Float = 0f
        private set

    var initialScrollCalculated = false
        private set

    fun setScroll(value: Float, minScroll: Float, maxScroll: Float) {
        scrollJob?.cancel()
        scrollJob = null
        this.minScroll = minScroll
        this.maxScroll = maxScroll
        if (currentScroll == 0f) setScroll(value)
        initialScrollCalculated = true
    }

    suspend fun getTargetScrollItem(): Int {
        scrollJob?.join()
        return currentItem
    }

    private fun setScroll(value: Float): Float {
        val currentScroll = currentScroll
        val delta = value - currentScroll
        onScrollDeltaInternal(delta)
        return delta
    }

    fun onScrollDelta(delta: Float) {
        currentScrollPriority = null
        scrollJob?.cancel()
        scrollJob = null
        onScrollDeltaInternal(delta)
    }

    private fun onScrollDeltaInternal(delta: Float) {
        val newScroll = (currentScroll + delta)
        scroll.floatValue = newScroll
        currentScroll = newScroll
    }

    private suspend fun stopScrollJob(scrollPriority: MutatePriority): Boolean {
        when {
            isDragInProgress -> return false
            (currentScrollPriority?.compareTo(scrollPriority) ?: 0) > 0 -> return false
        }
        scrollJob?.cancelAndJoin()
        scrollJob = null
        return true
    }

    private suspend fun startScroll(
        scrollPriority: MutatePriority,
        scroll: suspend CoroutineScope.() -> Unit,
    ) {
        if (stopScrollJob(scrollPriority)) {
            currentScrollPriority = scrollPriority
            scrollJob = coroutineScope {
                launch(context = Job()) {
                    scroll()
                    currentScrollPriority = null
                }
            }
        }
    }

    internal suspend fun snap(
        velocity: Float,
        scrollPriority: MutatePriority = MutatePriority.UserInput,
    ) {
        startScroll(scrollPriority) { performSnap(velocity) }
    }

    private suspend fun performSnap(velocity: Float) {
        val targetValue =
            FloatExponentialDecaySpec()
                .getTargetValue(currentScroll, velocity)
                .coerceIn(minScroll, maxScroll)
        val remainder = targetValue % maxItemHeight
        val snapScrollValue =
            targetValue - remainder - if (abs(remainder) > maxItemHeight / 2) maxItemHeight else 0
        animate(
            initialValue = currentScroll,
            targetValue = snapScrollValue,
            initialVelocity = velocity,
            animationSpec = scrollAnimationSpec,
        ) { value, _ ->
            setScroll(value)
        }
    }

    private fun getTargetScrollValue(index: Int, positionOffset: Float): Float =
        maxScroll - maxItemHeight * (index + positionOffset).coerceIn(0f, itemCount - 1f)

    suspend fun scrollTo(
        index: Int,
        positionOffset: Float = 0f,
        scrollPriority: MutatePriority = MutatePriority.Default,
    ) {
        startScroll(scrollPriority) { performScrollTo(index, positionOffset) }
    }

    private fun performScrollTo(index: Int, positionOffset: Float) {
        setScroll(getTargetScrollValue(index, positionOffset))
    }

    suspend fun animateScrollTo(
        index: Int,
        scrollPriority: MutatePriority = MutatePriority.Default,
    ) {
        startScroll(scrollPriority) { performAnimateScrollTo(index) }
    }

    private suspend fun performAnimateScrollTo(index: Int) {
        animate(
            initialValue = currentScroll,
            targetValue = getTargetScrollValue(index, 0f),
            animationSpec = scrollAnimationSpec,
        ) { value, _ ->
            setScroll(value)
        }
    }

    companion object : Saver<WheelPickerState, Bundle> {
        private const val KEY_SELECTED_INDEX = "selected_index"

        override fun restore(value: Bundle): WheelPickerState =
            WheelPickerState(initialSelectedIndex = value.getInt(KEY_SELECTED_INDEX))

        override fun SaverScope.save(value: WheelPickerState): Bundle =
            bundleOf(KEY_SELECTED_INDEX to value.currentItem)
    }
}

@Composable
fun rememberWheelPickerState(initialSelectedIndex: Int = 0): WheelPickerState =
    rememberSaveable(saver = WheelPickerState) { WheelPickerState(initialSelectedIndex) }

@LightAndDarkThemePreview
@Composable
fun WheelPickerPreview() {
    LiftAppTheme {
        Surface {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp),
            ) {
                WheelPicker(
                    items = {
                        val deselectedColor = MaterialTheme.colorScheme.onSurface
                        val selectedColor = MaterialTheme.colorScheme.primary

                        List(10) { it.toString() }
                            .forEach {
                                val positionOffset = remember { mutableFloatStateOf(1f) }
                                val textColor = remember { mutableStateOf(deselectedColor) }
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = textColor.value,
                                    modifier =
                                        Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                            .onPositionChange { offset, viewPortOffset ->
                                                positionOffset.floatValue = abs(viewPortOffset)
                                                textColor.value =
                                                    lerp(
                                                        selectedColor,
                                                        deselectedColor,
                                                        abs(offset),
                                                    )
                                            }
                                            .graphicsLayer {
                                                alpha =
                                                    0f + (1 - positionOffset.floatValue) // * .75f
                                                scaleY = .5f + (1 - positionOffset.floatValue) * .5f
                                            },
                                )
                            }
                    },
                    highlight = {
                        Box(
                            Modifier.border(
                                2.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(8.dp),
                            )
                        )
                    },
                )

                WheelPicker(
                    items = {
                        listOf("\uD83D\uDCAA", "\uD83D\uDC7E", "\uD83E\uDEF5").forEach { Text(it) }
                    },
                    state = rememberWheelPickerState(1),
                    highlight = {
                        Box(
                            Modifier.height(44.dp)
                                .aspectRatio(2f)
                                .border(
                                    width = 1.dp,
                                    brush =
                                        Brush.verticalGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = .3f),
                                                MaterialTheme.colorScheme.primary.copy(alpha = .1f),
                                            )
                                        ),
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .padding(horizontal = 4.dp)
                        )
                    },
                )
            }
        }
    }
}
