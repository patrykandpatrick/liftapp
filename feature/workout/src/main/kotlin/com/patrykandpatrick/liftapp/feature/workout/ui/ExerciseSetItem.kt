package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.prettyString
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.animation.visibilityChangeSpring
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.Alpha
import com.patrykandpatryk.liftapp.core.ui.theme.PillShape

@Composable
internal fun SetItem(
    setIndex: Int,
    set: EditableExerciseSet,
    isActive: Boolean,
    enabled: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember(isActive) { mutableStateOf(isActive) }
    Column(
        modifier = modifier
    ) {
        ListItem(
            icon = {
                SetNumber(
                    setIndex = setIndex,
                    isActive = isActive,
                    isComplete = set.isComplete,
                    focused = expanded,
                )
            },
            title = {
                Text(
                    text = set.prettyString(),
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = Alpha.get(enabled = enabled, focused = isActive || expanded),
                    ),
                )
            },
            actions = {
                AnimatedVisibility(visible = enabled) {
                    val rotation = animateFloatAsState(
                        targetValue = if (expanded) 180f else 0f,
                        animationSpec = visibilityChangeSpring(),
                        label = "Expand rotation",
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_expand_more),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.graphicsLayer { rotationZ = rotation.value },
                    )
                }
            },
            paddingValues = PaddingValues(
                horizontal = LocalDimens.current.padding.itemHorizontal,
                vertical = LocalDimens.current.padding.itemVerticalMedium,
            ),
            onClick = { if (enabled) expanded = !expanded },
        )

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(visibilityChangeSpring()) + expandVertically(visibilityChangeSpring(IntSize.VisibilityThreshold)),
            exit = fadeOut(visibilityChangeSpring()) + shrinkVertically(visibilityChangeSpring(IntSize.VisibilityThreshold)),
            modifier = Modifier.padding(start = 72.dp, end = 16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemVertical),
            ) {
                SetEditorContent(set)

                Button(
                    onClick = onSave,
                    enabled = set.isInputValid,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.action_save))
                }
            }
        }
    }
}

@Composable
private fun SetNumber(
    setIndex: Int,
    isActive: Boolean,
    isComplete: Boolean,
    focused: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderColor = animateColorAsState(
        targetValue = when {
            isActive -> Color.Transparent
            isComplete -> MaterialTheme.colorScheme.primary.copy(alpha = Alpha.get(focused = focused))
            else -> MaterialTheme.colorScheme.outlineVariant
        },
        label = "Border color"
    )

    val backgroundColor = animateColorAsState(
        targetValue = when {
            isActive -> MaterialTheme.colorScheme.primary
            else -> Color.Transparent
        },
        label = "Background color"
    )

    val textColor = animateColorAsState(
        targetValue = when {
            isActive -> MaterialTheme.colorScheme.onPrimary
            isComplete -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "Text color"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .border(1.5.dp, borderColor.value, PillShape)
            .background(backgroundColor.value, PillShape)
    ) {
        Text(
            text = (setIndex + 1).toString(),
            style = MaterialTheme.typography.titleSmall,
            color = textColor.value,
            textAlign = TextAlign.Center,
        )
    }
}
