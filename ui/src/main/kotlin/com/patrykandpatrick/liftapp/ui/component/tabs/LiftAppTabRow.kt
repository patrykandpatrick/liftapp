package com.patrykandpatrick.liftapp.ui.component.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.patrykandpatrick.liftapp.ui.preview.ComponentPreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun LiftAppTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    selectedTabOffset: Float? = null,
    indicator: @Composable LiftAppTabIndicatorScope.() -> Unit = {
        with(LiftAppTabRowDefaults) { Indicator() }
    },
    tabs: @Composable () -> Unit,
) {
    val animateChanges = selectedTabOffset == null
    val indicatorScope = remember(animateChanges) { LiftAppTabIndicatorScopeImpl(animateChanges) }
    SubcomposeLayout(modifier = modifier) { constraints ->
        val tabWidths = mutableListOf<Int>()
        val indicatorPositions = mutableListOf<Int>()
        val measurables = subcompose(slotId = 0, content = tabs)
        if (measurables.isEmpty()) return@SubcomposeLayout layout(constraints.maxWidth, 0) {}

        val childWidth = constraints.maxWidth / measurables.size
        var childConstraints = constraints.copy(maxWidth = childWidth)
        val tabReferencePlaceables = measurables.map { it.measure(childConstraints) }
        tabReferencePlaceables.forEach { tabWidths.add(it.width) }
        var height = tabReferencePlaceables.maxOfOrNull { it.height } ?: 0
        childConstraints =
            childConstraints.copy(minWidth = childWidth, minHeight = height, maxHeight = height)

        val indicatorConstraints =
            constraints.copy(
                minWidth = tabWidths[selectedTabIndex],
                maxWidth = tabWidths[selectedTabIndex],
            )
        val indicatorPlaceable =
            subcompose(slotId = "indicator") { indicator(indicatorScope) }
                .map { it.measure(indicatorConstraints) }

        height += (indicatorPlaceable.maxOfOrNull { it.height } ?: 0)

        layout(constraints.maxWidth, height) {
            subcompose(slotId = 1, content = tabs).forEachIndexed { index, measurable ->
                val placeable = measurable.measure(childConstraints)
                val x = childWidth * index
                placeable.placeRelative(x, 0)
                indicatorPositions += x + (placeable.width - tabWidths[index]) / 2
            }

            indicatorPlaceable.forEach {
                val node = it.parentData as? IndicatorBehaviorNode
                val indicatorPosition: Int
                val indicatorWidth: Int

                if (selectedTabOffset != null) {
                    val baseIndicatorPosition = indicatorPositions[selectedTabIndex]
                    val offsetTabIndex = selectedTabIndex.getOffsetIndex(selectedTabOffset)
                    val offsetDistance =
                        (indicatorPositions[offsetTabIndex] - baseIndicatorPosition) *
                            selectedTabOffset.absoluteValue
                    indicatorPosition = (baseIndicatorPosition + offsetDistance).roundToInt()

                    indicatorWidth =
                        lerp(
                            start = tabWidths[selectedTabIndex],
                            stop = tabWidths[offsetTabIndex],
                            fraction = selectedTabOffset.absoluteValue,
                        )
                } else {
                    indicatorPosition = indicatorPositions[selectedTabIndex]
                    indicatorWidth = tabWidths[selectedTabIndex]
                }
                if (node != null) {
                    node.offset.intValue = indicatorPosition
                    node.width.intValue = indicatorWidth
                    it.placeRelative(0, height - it.height)
                } else {
                    it.placeRelative(indicatorPosition, height - it.height)
                }
            }
        }
    }
}

object LiftAppTabRowDefaults {

    @Composable
    fun LiftAppTabIndicatorScope.Indicator(modifier: Modifier = Modifier) {
        Box(
            modifier
                .indicatorBehavior()
                .height(3.dp)
                .background(colorScheme.primary, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
        )
    }
}

private fun Int.getOffsetIndex(offset: Float): Int =
    when {
        offset > 0f -> this + 1
        offset < 0f -> this - 1
        else -> this
    }

private class LiftAppTabIndicatorScopeImpl(private val animateChanges: Boolean) :
    LiftAppTabIndicatorScope {
    override fun Modifier.indicatorBehavior(): Modifier =
        this.then(IndicatorBehaviorElement(animateChanges))
}

@ComponentPreview
@Composable
private fun LiftAppTabRowPreview() {
    LiftAppTheme {
        val (selectedIndex, setSelectedIndex) = remember { mutableIntStateOf(0) }
        LiftAppTabRow(
            selectedTabIndex = selectedIndex,
            modifier = Modifier.systemBarsPadding().background(colorScheme.background),
        ) {
            LiftAppTabRowItem(selected = selectedIndex == 0, onClick = { setSelectedIndex(0) }) {
                Text("Test")
            }
            LiftAppTabRowItem(selected = selectedIndex == 1, onClick = { setSelectedIndex(1) }) {
                Text("Test2siemaaa")
            }
        }
    }
}
