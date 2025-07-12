package com.patrykandpatrick.liftapp.ui.modifier

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.interaction.HoverInteraction
import com.patrykandpatrick.liftapp.ui.state.animatedColorStateOf
import kotlin.properties.Delegates
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun Modifier.interactiveBorder(
    interactionSource: InteractionSource,
    colors: InteractiveBorderColors,
    shape: Shape,
    width: Dp = 1.dp,
    checked: Boolean = false,
    animationSpec: AnimationSpec<Color> = spring(),
    maxWidth: Dp? = null,
    maxHeight: Dp? = null,
): Modifier =
    this.then(
        InteractiveBorderElement(
            interactionSource,
            colors,
            shape,
            width,
            checked,
            animationSpec,
            maxWidth,
            maxHeight,
        )
    )

private data class InteractiveBorderElement(
    private val interactionSource: InteractionSource,
    private val colors: InteractiveBorderColors,
    private val shape: Shape,
    private val strokeWidth: Dp,
    private val checked: Boolean,
    private val animationSpec: AnimationSpec<Color>,
    private val maxWidth: Dp?,
    private val maxHeight: Dp?,
) : ModifierNodeElement<BorderNode>() {
    override fun create(): BorderNode =
        BorderNode(
            interactionSource,
            colors,
            strokeWidth,
            checked,
            shape,
            animationSpec,
            maxWidth,
            maxHeight,
        )

    override fun update(node: BorderNode) {
        node.interactionSource = interactionSource
        node.colors = colors
        node.shape = shape
        node.strokeWidth = strokeWidth
        node.checked = checked
        node.animationSpec = animationSpec
        node.maxWidth = maxWidth
        node.maxHeight = maxHeight
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "Interactive Border"
        properties["interactionSource"] = interactionSource
        properties["colors"] = colors
        properties["shape"] = shape
        properties["width"] = strokeWidth
        properties["checked"] = checked
        properties["maxWidth"] = maxWidth
        properties["maxHeight"] = maxHeight
    }
}

private class BorderNode(
    var interactionSource: InteractionSource,
    var colors: InteractiveBorderColors,
    var strokeWidth: Dp,
    var checked: Boolean,
    shape: Shape,
    animationSpec: AnimationSpec<Color>,
    var maxWidth: Dp?,
    var maxHeight: Dp?,
) : DrawModifierNode, Modifier.Node() {

    private val idleColor: Color
        get() = if (checked) colors.checkedColor else colors.color

    var shape: Shape by Delegates.observable(shape) { _, _, _ -> updateBorderColors() }

    var animationSpec: AnimationSpec<Color> by
        Delegates.observable(animationSpec) { _, _, _ -> updateBorderColors() }

    private var borderPrimaryColor = animatedColorStateOf(idleColor, animationSpec)

    private var borderSecondaryColor = animatedColorStateOf(idleColor, animationSpec)

    private fun updateBorderColors() {
        borderPrimaryColor = animatedColorStateOf(idleColor, animationSpec)
        borderSecondaryColor = animatedColorStateOf(idleColor, animationSpec)
    }

    private val touchOffset = mutableStateOf(Offset.Companion.Zero)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                coroutineScope {
                    when (interaction) {
                        is PressInteraction.Press -> {
                            touchOffset.value = interaction.pressPosition
                            launch { borderPrimaryColor.animate(colors.pressedColor) }
                            launch { borderSecondaryColor.animate(colors.pressedColor) }
                        }

                        is HoverInteraction.Enter -> {
                            touchOffset.value = interaction.position
                            launch { borderPrimaryColor.animate(colors.hoverForegroundColor) }
                            launch { borderSecondaryColor.animate(colors.hoverBackgroundColor) }
                        }

                        is HoverInteraction.EnterFromRelease -> {
                            touchOffset.value = interaction.position
                            launch {
                                borderPrimaryColor.animate(colors.hoverForegroundColor)
                                borderPrimaryColor.animate(colors.pressedColor)
                            }
                            launch {
                                borderSecondaryColor.animate(colors.pressedColor)
                                borderSecondaryColor.animate(colors.hoverBackgroundColor)
                            }
                        }

                        is PressInteraction.Release -> {
                            launch {
                                borderPrimaryColor.animate(colors.pressedColor)
                                borderPrimaryColor.animate(idleColor)
                            }
                            launch {
                                borderSecondaryColor.animate(colors.pressedColor)
                                borderSecondaryColor.animate(idleColor)
                            }
                        }

                        is DragInteraction.Start -> {
                            launch { borderPrimaryColor.animate(colors.draggedColor) }
                            launch { borderSecondaryColor.animate(colors.draggedColor) }
                        }

                        is DragInteraction.Stop,
                        is DragInteraction.Cancel,
                        is HoverInteraction.Exit,
                        is PressInteraction.Cancel -> {
                            launch { borderPrimaryColor.animate(idleColor) }
                            launch { borderSecondaryColor.animate(idleColor) }
                        }
                    }
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        val borderWidth = strokeWidth.roundToPx()
        val actualMaxWidth = size.width - borderWidth
        val actualMaxHeight = size.height - borderWidth
        val width = maxWidth?.toPx()?.coerceAtMost(actualMaxWidth) ?: actualMaxWidth
        val height = maxHeight?.toPx()?.coerceAtMost(actualMaxHeight) ?: actualMaxHeight
        val outline =
            shape.createOutline(
                size = Size(width, height),
                layoutDirection = layoutDirection,
                density = this,
            )
        drawContent()
        translate((size.width - width) / 2, (size.height - height) / 2) {
            drawOutline(
                outline = outline,
                brush =
                    Brush.Companion.radialGradient(
                        colors = listOf(borderPrimaryColor.value, borderSecondaryColor.value),
                        center = touchOffset.value,
                        radius = size.maxDimension / 2,
                    ),
                style = Stroke(strokeWidth.roundToPx().toFloat()),
            )
        }
    }
}
