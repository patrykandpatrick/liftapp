package com.patrykandpatrick.liftapp.ui.modifier

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import com.patrykandpatrick.liftapp.ui.interaction.HoverInteraction
import com.patrykandpatrick.liftapp.ui.state.animatedFloatStateOf
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun Modifier.interactiveScale(
    interactionSource: InteractionSource,
    scale: IndicationScale = IndicationScale(),
    animationSpec: AnimationSpec<Float> = spring(),
): Modifier = this.then(InteractiveScaleElement(interactionSource, scale, animationSpec))

data class IndicationScale(
    val hover: Float = 1.02f,
    val press: Float = .95f,
    val default: Float = 1f,
)

private data class InteractiveScaleElement(
    private val interactionSource: InteractionSource,
    private val scale: IndicationScale,
    private val animationSpec: AnimationSpec<Float>,
) : ModifierNodeElement<ScaleNode>() {
    override fun create(): ScaleNode = ScaleNode(interactionSource, scale, animationSpec)

    override fun update(node: ScaleNode) {
        node.interactionSource = interactionSource
        node.scale = scale
        node.animationSpec = animationSpec
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
    var interactionSource: InteractionSource,
    var scale: IndicationScale,
    var animationSpec: AnimationSpec<Float>,
) : DrawModifierNode, Modifier.Node() {

    private val currentScale = animatedFloatStateOf(1f, animationSpec)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> currentScale.animate(scale.press)
                    is HoverInteraction.Enter -> currentScale.animate(scale.hover)
                    is PressInteraction.Release -> {
                        withContext(NonCancellable) { currentScale.animate(scale.press) }
                        currentScale.animate(scale.default)
                    }
                    is HoverInteraction.Exit,
                    is PressInteraction.Cancel -> {
                        currentScale.animate(scale.default)
                    }
                    else -> {}
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        scale(currentScale.value) { with(this@draw) { drawContent() } }
    }
}
