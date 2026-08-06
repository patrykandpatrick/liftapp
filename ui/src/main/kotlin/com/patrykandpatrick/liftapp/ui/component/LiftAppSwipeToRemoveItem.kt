package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.PillShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

@Composable
fun LiftAppSwipeToRemoveItem(
    position: LiftAppListItemPosition,
    removeLabel: String,
    onRemove: (onCanceled: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (expanded: Boolean) -> Unit,
) {
    val state = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()
    val currentOnRemove by rememberUpdatedState(onRemove)
    val onDismiss =
        remember(state, scope) {
            { direction: SwipeToDismissBoxValue ->
                if (direction == SwipeToDismissBoxValue.EndToStart) {
                    currentOnRemove { scope.launch { state.reset() } }
                }
            }
        }
    val expanded = state.dismissDirection != SwipeToDismissBoxValue.Settled
    val gapAfter = if (position.index == position.count - 1) 0.dp else LiftAppListItemDefaults.gap
    val density = LocalDensity.current
    val swipeOffsetPx = if (expanded) state.requireOffset() else 0f
    val revealWidth =
        with(density) { (swipeOffsetPx.absoluteValue.toDp() - ActionSpacing).coerceAtLeast(0.dp) }
    val pinnedIconTranslation =
        with(density) {
            if (revealWidth < MinimumActionWidth) {
                ((revealWidth - MinimumActionWidth) / 2).toPx()
            } else {
                0f
            }
        }

    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize().padding(end = ActionSpacing, bottom = gapAfter),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Box(
                    modifier =
                        Modifier.fillMaxHeight()
                            .width(revealWidth)
                            .clip(PillShape)
                            .background(colorScheme.error),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = LiftAppIcons.Delete,
                        contentDescription = null,
                        tint = colorScheme.onError,
                        modifier =
                            Modifier.requiredSize(ActionIconSize).graphicsLayer {
                                // Keep the icon fixed in its 48 dp slot until the capsule is wide
                                // enough to center it without clipping.
                                translationX = pinnedIconTranslation
                            },
                    )
                }
            }
        },
        modifier =
            modifier.fillMaxWidth().dismissalAnchorOverhang(ActionSpacing).semantics {
                customActions =
                    listOf(
                        CustomAccessibilityAction(removeLabel) {
                            currentOnRemove { scope.launch { state.reset() } }
                            true
                        }
                    )
            },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        onDismiss = onDismiss,
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(end = ActionSpacing)) { content(expanded) }
    }
}

private fun Modifier.dismissalAnchorOverhang(overhang: Dp) = layout { measurable, constraints ->
    val overhangPx = overhang.roundToPx()
    // Material anchors dismissal at the measured width. Measure the swipe container wider, but
    // report its original width to the list, so the item tracks the finger exactly and settles
    // `overhang` beyond the list bounds.
    val widenedConstraints =
        if (constraints.hasBoundedWidth) {
            constraints.copy(
                minWidth = constraints.minWidth + overhangPx,
                maxWidth = constraints.maxWidth + overhangPx,
            )
        } else {
            constraints
        }
    val placeable = measurable.measure(widenedConstraints)
    val reportedWidth =
        if (constraints.hasBoundedWidth) placeable.width - overhangPx else placeable.width
    layout(reportedWidth, placeable.height) { placeable.placeRelative(0, 0) }
}

private val MinimumActionWidth = 48.dp
private val ActionIconSize = 24.dp
private val ActionSpacing = 2.dp
