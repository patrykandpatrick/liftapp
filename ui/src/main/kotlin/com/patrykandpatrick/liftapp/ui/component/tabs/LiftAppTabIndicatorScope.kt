package com.patrykandpatrick.liftapp.ui.component.tabs

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

interface LiftAppTabIndicatorScope {
    fun Modifier.indicatorBehavior(): Modifier
}

internal class IndicatorBehaviorElement(private val animateChanges: Boolean) :
    ModifierNodeElement<IndicatorBehaviorNode>() {
    private val offset = mutableIntStateOf(0)
    private val width = mutableIntStateOf(0)

    override fun create(): IndicatorBehaviorNode =
        IndicatorBehaviorNode(offset, width, animateChanges)

    override fun update(node: IndicatorBehaviorNode) {
        node.offset = offset
        node.width = width
        node.animateChanges = animateChanges
    }

    override fun hashCode(): Int = 1

    override fun equals(other: Any?): Boolean = other is IndicatorBehaviorElement

    override fun InspectorInfo.inspectableProperties() {
        name = "LiftAppTabIndicator"
        properties["offset"] = offset.intValue
        properties["width"] = width.intValue
        properties["animateChanges"] = animateChanges
    }
}

internal class IndicatorBehaviorNode(
    var offset: MutableIntState,
    var width: MutableIntState,
    var animateChanges: Boolean,
) : Modifier.Node(), ParentDataModifierNode, DrawModifierNode, LayoutModifierNode {

    private val animatedOffset = mutableFloatStateOf(-1f)
    private val animatedWidth = mutableIntStateOf(-1)

    override fun Density.modifyParentData(parentData: Any?): Any? = this@IndicatorBehaviorNode

    override fun onAttach() {
        var lastOffsetVelocity = 0f
        var lastWidthVelocity = 0

        coroutineScope.launch {
            launch {
                snapshotFlow { offset.intValue }
                    .collectLatest { offset ->
                        if (!animateChanges || animatedOffset.floatValue == -1f) {
                            animatedOffset.floatValue = offset.toFloat()
                        } else {
                            animate(
                                initialValue = animatedOffset.floatValue,
                                targetValue = offset.toFloat(),
                                initialVelocity = lastOffsetVelocity,
                                animationSpec =
                                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow),
                            ) { value, velocity ->
                                lastOffsetVelocity = velocity
                                animatedOffset.floatValue = value
                            }
                        }
                    }
            }
            launch {
                snapshotFlow { width.intValue }
                    .collectLatest { width ->
                        if (!animateChanges || animatedWidth.intValue == -1) {
                            animatedWidth.intValue = width
                        } else {
                            animate(
                                initialValue = animatedWidth.intValue,
                                targetValue = width,
                                initialVelocity = lastWidthVelocity,
                                typeConverter = Int.Companion.VectorConverter,
                            ) { value, velocity ->
                                lastWidthVelocity = velocity
                                animatedWidth.intValue = value
                            }
                        }
                    }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        translate(animatedOffset.floatValue) { with(this@draw) { drawContent() } }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val x = -(constraints.minWidth - animatedWidth.intValue) / 2
        val minWidth =
            if (animatedWidth.intValue >= 0) {
                animatedWidth.intValue
            } else {
                constraints.minWidth
            }
        val placeable =
            measurable.measure(
                constraints.copy(
                    minWidth = minWidth,
                    maxWidth = constraints.maxWidth.coerceAtLeast(minWidth),
                )
            )
        return layout(placeable.width, placeable.height) { placeable.placeRelative(x, 0) }
    }
}
