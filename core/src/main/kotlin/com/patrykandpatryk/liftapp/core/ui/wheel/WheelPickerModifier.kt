package com.patrykandpatryk.liftapp.core.ui.wheel

import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.unit.Density

interface WheelPickerScope {
    fun Modifier.onPositionChange(
        onOffsetChange: ((offset: Float, viewPortOffset: Float) -> Unit)?,
    ) = this
}

internal class WheelPickerScopeImpl : WheelPickerScope {
    override fun Modifier.onPositionChange(
        onOffsetChange: ((offset: Float, viewPortOffset: Float) -> Unit)?,
    ): Modifier = this.then(
        ListPickerItemSpecElement(
            onPositionChange = onOffsetChange,
        )
    )
}

internal data class ListPickerItemSpecElement(
    private val onPositionChange: ((offset: Float, viewPortOffset: Float) -> Unit)?,
) : ModifierNodeElement<ListPickerItemSpecNode>() {
    override fun create(): ListPickerItemSpecNode =
        ListPickerItemSpecNode(onPositionChange)

    override fun update(node: ListPickerItemSpecNode) {
        node.onPositionChange = onPositionChange
    }
}

internal class ListPickerItemSpecNode(
    var onPositionChange: ((offset: Float, viewPortOffset: Float) -> Unit)?,
) : Modifier.Node(), ParentDataModifierNode {
    override fun Density.modifyParentData(parentData: Any?): Any {
        return this@ListPickerItemSpecNode
    }
}
