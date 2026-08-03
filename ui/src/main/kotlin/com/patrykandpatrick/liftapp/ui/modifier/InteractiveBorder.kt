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
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.interaction.HoverInteraction
import com.patrykandpatrick.liftapp.ui.state.animatedColorStateOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun Modifier.interactiveBorder(
    interactionSource: InteractionSource,
    colors: InteractiveBorderColors,
    shape: Shape,
    width: Dp = 1.dp,
    horizontalInset: Dp = 0.dp,
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
            horizontalInset,
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
    private val horizontalInset: Dp,
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
            horizontalInset,
            checked,
            shape,
            animationSpec,
            maxWidth,
            maxHeight,
        )

    override fun update(node: BorderNode) {
        node.update(
            interactionSource = interactionSource,
            colors = colors,
            shape = shape,
            strokeWidth = strokeWidth,
            horizontalInset = horizontalInset,
            checked = checked,
            animationSpec = animationSpec,
            maxWidth = maxWidth,
            maxHeight = maxHeight,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "Interactive Border"
        properties["interactionSource"] = interactionSource
        properties["colors"] = colors
        properties["shape"] = shape
        properties["width"] = strokeWidth
        properties["horizontalInset"] = horizontalInset
        properties["checked"] = checked
        properties["maxWidth"] = maxWidth
        properties["maxHeight"] = maxHeight
    }
}

private class BorderNode(
    private var interactionSource: InteractionSource,
    private var colors: InteractiveBorderColors,
    private var strokeWidth: Dp,
    private var horizontalInset: Dp,
    private var checked: Boolean,
    private var shape: Shape,
    private var animationSpec: AnimationSpec<Color>,
    private var maxWidth: Dp?,
    private var maxHeight: Dp?,
) : DrawModifierNode, Modifier.Node() {

    private val idleColor: Color
        get() = if (checked) colors.checkedColor else colors.color

    private var borderPrimaryColor = animatedColorStateOf(idleColor, animationSpec)

    private var borderSecondaryColor = animatedColorStateOf(idleColor, animationSpec)

    private var interactionCollectionJob: Job? = null
    private var pressAnimationJob: Job? = null
    private var postPressAnimationJob: Job? = null
    private var isPressed = false
    private val activeDrags = mutableSetOf<DragInteraction.Start>()

    private fun resetBorderColors() {
        borderPrimaryColor = animatedColorStateOf(idleColor, animationSpec)
        borderSecondaryColor = animatedColorStateOf(idleColor, animationSpec)
    }

    private val touchOffset = mutableStateOf(Offset.Companion.Zero)

    private suspend fun animateBorder(primary: Color, secondary: Color) {
        coroutineScope {
            launch { borderPrimaryColor.animate(primary) }
            launch { borderSecondaryColor.animate(secondary) }
        }
    }

    private fun animatePress() {
        postPressAnimationJob?.cancel()
        pressAnimationJob?.cancel()
        pressAnimationJob = coroutineScope.launch {
            animateBorder(colors.pressedColor, colors.pressedColor)
        }
    }

    private fun animateAfterPress(targetColors: () -> Pair<Color, Color>) {
        postPressAnimationJob?.cancel()
        val pressAnimationJob = pressAnimationJob
        postPressAnimationJob = coroutineScope.launch {
            pressAnimationJob?.join()
            val (primary, secondary) = targetColors()
            animateBorder(primary, secondary)
        }
    }

    private fun animateImmediately(primary: Color, secondary: Color = primary) {
        pressAnimationJob?.cancel()
        postPressAnimationJob?.cancel()
        postPressAnimationJob = coroutineScope.launch { animateBorder(primary, secondary) }
    }

    override fun onAttach() {
        resetBorderColors()
        observeInteractions()
    }

    override fun onDetach() {
        interactionCollectionJob?.cancel()
        pressAnimationJob?.cancel()
        postPressAnimationJob?.cancel()
        isPressed = false
        activeDrags.clear()
        resetBorderColors()
    }

    fun update(
        interactionSource: InteractionSource,
        colors: InteractiveBorderColors,
        shape: Shape,
        strokeWidth: Dp,
        horizontalInset: Dp,
        checked: Boolean,
        animationSpec: AnimationSpec<Color>,
        maxWidth: Dp?,
        maxHeight: Dp?,
    ) {
        val interactionSourceChanged = this.interactionSource !== interactionSource
        val animationSpecChanged = this.animationSpec != animationSpec
        val idleColorChanged =
            this.colors != colors || this.checked != checked || animationSpecChanged
        val currentPrimaryColor = borderPrimaryColor.value
        val currentSecondaryColor = borderSecondaryColor.value

        this.interactionSource = interactionSource
        this.colors = colors
        this.shape = shape
        this.strokeWidth = strokeWidth
        this.horizontalInset = horizontalInset
        this.checked = checked
        this.animationSpec = animationSpec
        this.maxWidth = maxWidth
        this.maxHeight = maxHeight

        if (idleColorChanged) {
            if (isAttached) {
                if (animationSpecChanged) {
                    borderPrimaryColor = animatedColorStateOf(currentPrimaryColor, animationSpec)
                    borderSecondaryColor =
                        animatedColorStateOf(currentSecondaryColor, animationSpec)
                }
                animateImmediately(idleColor)
            } else {
                resetBorderColors()
            }
        }
        if (interactionSourceChanged && isAttached) observeInteractions()
        invalidateDraw()
    }

    private fun observeInteractions() {
        interactionCollectionJob?.cancel()
        pressAnimationJob?.cancel()
        postPressAnimationJob?.cancel()
        isPressed = false
        activeDrags.clear()
        interactionCollectionJob = coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        isPressed = true
                        touchOffset.value = interaction.pressPosition
                        animatePress()
                    }

                    is HoverInteraction.Enter -> {
                        touchOffset.value = interaction.position
                        animateImmediately(
                            colors.hoverForegroundColor,
                            colors.hoverBackgroundColor,
                        )
                    }

                    is HoverInteraction.EnterFromRelease -> {
                        touchOffset.value = interaction.position
                        animateAfterPress {
                            colors.hoverForegroundColor to colors.hoverBackgroundColor
                        }
                    }

                    is PressInteraction.Release -> {
                        isPressed = false
                        animateAfterPress {
                            val targetColor =
                                if (activeDrags.isEmpty()) idleColor else colors.draggedColor
                            targetColor to targetColor
                        }
                    }

                    is DragInteraction.Start -> {
                        isPressed = false
                        activeDrags += interaction
                        animateImmediately(colors.draggedColor)
                    }

                    is DragInteraction.Stop -> {
                        activeDrags -= interaction.start
                        val targetColor =
                            if (activeDrags.isEmpty()) idleColor else colors.draggedColor
                        animateImmediately(targetColor)
                    }

                    is DragInteraction.Cancel -> {
                        activeDrags -= interaction.start
                        val targetColor =
                            if (activeDrags.isEmpty()) idleColor else colors.draggedColor
                        animateImmediately(targetColor)
                    }

                    is HoverInteraction.Exit,
                    is PressInteraction.Cancel -> {
                        isPressed = false
                        val targetColor =
                            if (activeDrags.isEmpty()) idleColor else colors.draggedColor
                        animateImmediately(targetColor)
                    }
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        val borderWidth = strokeWidth.roundToPx()
        val actualMaxWidth = size.width - borderWidth - horizontalInset.toPx() * 2
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
