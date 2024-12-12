package com.patrykandpatryk.liftapp.core.ui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    repeatLongClicks: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    content: @Composable () -> Unit,
) {
    val minSize = LocalDimens.current.iconButton.minTouchTarget

    interactionSource.onRepeatedLongPress(repeatLongClicks = repeatLongClicks) {
        onLongClick?.invoke()
    }

    Box(
        modifier =
            modifier
                .defaultMinSize(minWidth = minSize, minHeight = minSize)
                .background(color = colors.containerColor(enabled).value)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {}, // This is handled by `InteractionSource#onRepeatedLongPress`.
                    enabled = enabled,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication =
                        ripple(
                            bounded = false,
                            radius = LocalDimens.current.iconButton.rippleRadius,
                        ),
                ),
        contentAlignment = Alignment.Center,
    ) {
        val contentColor = colors.contentColor(enabled).value
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}
