package com.patrykandpatrick.liftapp.ui.modifier

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import com.patrykandpatrick.liftapp.ui.interaction.HoverInteraction
import com.patrykandpatrick.liftapp.ui.state.animatedFloatStateOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun Modifier.interactiveScale(
    interactionSource: InteractionSource,
    scale: IndicationScale = IndicationScale(),
    animationSpec: AnimationSpec<Float> = spring(),
): Modifier = this.then(InteractiveScaleElement(interactionSource, scale, animationSpec))

data class IndicationScale(
    val hover: Float = 1.02f,
    val press: Float = .95f,
    val drag: Float = press,
    val default: Float = 1f,
)

private data class InteractiveScaleElement(
    private val interactionSource: InteractionSource,
    private val scale: IndicationScale,
    private val animationSpec: AnimationSpec<Float>,
) : ModifierNodeElement<ScaleNode>() {
    override fun create(): ScaleNode = ScaleNode(interactionSource, scale, animationSpec)

    override fun update(node: ScaleNode) {
        node.update(interactionSource, scale, animationSpec)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "Interactive Scale"
    }
}

data class ScaleIndication(
    private val scale: IndicationScale = IndicationScale(),
    private val animationSpec: AnimationSpec<Float> = spring(),
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        ScaleNode(interactionSource, scale, animationSpec)
}

private class ScaleNode(
    private var interactionSource: InteractionSource,
    private var scale: IndicationScale,
    private var animationSpec: AnimationSpec<Float>,
) : DrawModifierNode, Modifier.Node() {

    private var currentScale = animatedFloatStateOf(1f, animationSpec)
    private val activeDrags = mutableSetOf<DragInteraction.Start>()
    private var interactionCollectionJob: Job? = null
    private var pressAnimationJob: Job? = null
    private var postPressAnimationJob: Job? = null

    private fun animatePress() {
        postPressAnimationJob?.cancel()
        pressAnimationJob?.cancel()
        pressAnimationJob = coroutineScope.launch { currentScale.animate(scale.press) }
    }

    private fun animateAfterPress(target: Float) {
        postPressAnimationJob?.cancel()
        val pressAnimationJob = pressAnimationJob
        postPressAnimationJob = coroutineScope.launch {
            pressAnimationJob?.join()
            currentScale.animate(target)
        }
    }

    private fun animateImmediately(target: Float) {
        pressAnimationJob?.cancel()
        postPressAnimationJob?.cancel()
        postPressAnimationJob = coroutineScope.launch { currentScale.animate(target) }
    }

    override fun onAttach() {
        currentScale.value = scale.default
        observeInteractions()
    }

    private fun observeInteractions() {
        interactionCollectionJob?.cancel()
        pressAnimationJob?.cancel()
        postPressAnimationJob?.cancel()
        activeDrags.clear()
        currentScale.value = scale.default
        interactionCollectionJob = coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> animatePress()
                    is HoverInteraction.Enter -> animateImmediately(scale.hover)
                    is HoverInteraction.EnterFromRelease -> animateAfterPress(scale.hover)
                    is PressInteraction.Release ->
                        animateAfterPress(if (activeDrags.isEmpty()) scale.default else scale.drag)
                    is DragInteraction.Start -> {
                        activeDrags += interaction
                        animateImmediately(scale.drag)
                    }
                    is DragInteraction.Stop -> {
                        activeDrags -= interaction.start
                        animateImmediately(if (activeDrags.isEmpty()) scale.default else scale.drag)
                    }
                    is DragInteraction.Cancel -> {
                        activeDrags -= interaction.start
                        animateImmediately(if (activeDrags.isEmpty()) scale.default else scale.drag)
                    }
                    is HoverInteraction.Exit,
                    is PressInteraction.Cancel ->
                        animateImmediately(if (activeDrags.isEmpty()) scale.default else scale.drag)
                    else -> Unit
                }
            }
        }
    }

    override fun onDetach() {
        interactionCollectionJob?.cancel()
        activeDrags.clear()
        pressAnimationJob?.cancel()
        postPressAnimationJob?.cancel()
    }

    fun update(
        interactionSource: InteractionSource,
        scale: IndicationScale,
        animationSpec: AnimationSpec<Float>,
    ) {
        val interactionSourceChanged = this.interactionSource !== interactionSource
        val animationSpecChanged = this.animationSpec != animationSpec
        val currentValue = currentScale.value

        this.interactionSource = interactionSource
        this.scale = scale
        this.animationSpec = animationSpec

        if (animationSpecChanged) {
            currentScale = animatedFloatStateOf(currentValue, animationSpec)
        }
        if (interactionSourceChanged && isAttached) observeInteractions()
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        scale(currentScale.value) { with(this@draw) { drawContent() } }
    }
}
