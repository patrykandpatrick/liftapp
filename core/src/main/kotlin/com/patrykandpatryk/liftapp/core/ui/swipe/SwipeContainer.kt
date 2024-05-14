package com.patrykandpatryk.liftapp.core.ui.swipe

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.zIndex
import com.patrykandpatrick.vico.core.extension.orZero
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.pixels
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import kotlin.math.abs

@Composable
fun SwipeContainer(
    dismissContent: @Composable (swipeProgress: Float, swipeOffset: Float) -> Unit,
    background: @Composable (swipeProgress: Float, swipeOffset: Float) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    val dimens = LocalDimens.current
    val density = LocalDensity.current
    val velocityThreshold = with(density) { dimens.swipe.velocityThreshold.toPx() }

    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = SwipeContainerState.Idle,
            positionalThreshold = { dimens.swipe.fractionalThreshold * it },
            velocityThreshold = { velocityThreshold },
            snapAnimationSpec = spring(),
            decayAnimationSpec = splineBasedDecay(density)
        )
    }

    val swipeOffset = anchoredDraggableState.offset.takeIf { it.isFinite() }.orZero
    var containerWidth by remember { mutableFloatStateOf(1f) }
    val swipeProgress = swipeOffset / containerWidth
    val isDismissed = abs(swipeProgress) == 1f
    val animatedHeight by animateFloatAsState(targetValue = if (isDismissed) 0f else 1f)

    LaunchedEffect(containerWidth) {
        anchoredDraggableState.updateAnchors(
            DraggableAnchors {
                SwipeContainerState.SwipedLeft at -containerWidth
                SwipeContainerState.Idle at 0f
                SwipeContainerState.SwipedRight at containerWidth
            },
        )
    }

    LaunchedEffect(key1 = isDismissed, key2 = animatedHeight) {
        if (isDismissed && animatedHeight == 0f) onDismiss()
    }

    Layout(
        content = {
            dismissContent(swipeProgress, swipeOffset)
            background(swipeProgress, swipeOffset)
        },
        modifier = modifier
            .anchoredDraggable(anchoredDraggableState, Orientation.Horizontal)
            .alpha(animatedHeight)
            .zIndex(if (swipeProgress != 0f) 1f else 0f),
    ) { measurables, constraints ->
        val foregroundPlaceable = measurables[0].measure(constraints)

        val backgroundPlaceable =
            measurables[1].measure(Constraints.fixed(foregroundPlaceable.width, foregroundPlaceable.height))

        containerWidth = foregroundPlaceable.width.toFloat()

        val height = foregroundPlaceable.height

        layout(
            width = foregroundPlaceable.width,
            height = (height * animatedHeight).toInt(),
        ) {
            val y = -(height * (1f - animatedHeight) / 2f).toInt()
            backgroundPlaceable.place(0, y)
            foregroundPlaceable.place(swipeOffset.toInt(), y)
        }
    }
}

@Composable
fun SwipeableDeleteBackground(
    swipeProgress: Float,
    swipeOffset: Float,
) {
    SwipeableBackground(
        swipeProgress = swipeProgress,
        swipeOffset = swipeOffset,
    ) { _, _ ->
        SwipeableBackgroundContent(
            swipeProgress = swipeProgress,
            swipeOffset = swipeOffset,
            icon = painterResource(id = R.drawable.ic_delete),
        )
    }
}

@Composable
fun SwipeableBackground(
    swipeProgress: Float,
    swipeOffset: Float,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable BoxScope.(swipeProgress: Float, swipeOffset: Float) -> Unit,
) {
    Box(
        modifier = Modifier
            .background(backgroundColor),
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content(swipeProgress, swipeOffset)
        }
    }
}

@Composable
fun BoxScope.SwipeableBackgroundContent(
    swipeProgress: Float,
    swipeOffset: Float,
    icon: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val alignment = remember(swipeProgress > 0f) {
        if (swipeProgress > 0f) Alignment.CenterStart else Alignment.CenterEnd
    }

    val backgroundVisibilityThreshold = LocalDimens.current.swipe.backgroundVisibilityThreshold.pixels

    val scaleAndAlpha = (abs(swipeOffset) / backgroundVisibilityThreshold).coerceAtMost(1f)

    Spacer(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = 1f - scaleAndAlpha))
            .fillMaxSize(),
    )

    Icon(
        painter = icon,
        contentDescription = contentDescription,
        modifier = modifier
            .graphicsLayer {
                scaleX = scaleAndAlpha
                scaleY = scaleAndAlpha
                alpha = scaleAndAlpha
            }
            .align(alignment)
            .padding(horizontal = LocalDimens.current.padding.contentHorizontal),
    )
}

enum class SwipeContainerState {
    Idle,
    SwipedLeft,
    SwipedRight,
}
