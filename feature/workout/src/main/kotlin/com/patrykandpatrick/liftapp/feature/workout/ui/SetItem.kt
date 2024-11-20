package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.theme.PillShape

enum class SetItemState {
    NotStarted,
    InProgress,
    Completed,
}

@Composable
internal fun SetItem(
    setIndex: Int,
    setItemState: SetItemState,
    modifier: Modifier = Modifier,
) {
    val borderColor = animateColorAsState(
        targetValue = when (setItemState) {
            SetItemState.NotStarted -> MaterialTheme.colorScheme.outlineVariant
            SetItemState.InProgress -> Color.Transparent
            SetItemState.Completed -> MaterialTheme.colorScheme.primary
        },
        label = "Border color"
    )

    val backgroundColor = animateColorAsState(
        targetValue = when (setItemState) {
            SetItemState.InProgress -> MaterialTheme.colorScheme.primary
            else -> Color.Transparent
        },
        label = "Background color"
    )

    val textColor = animateColorAsState(
        targetValue = when (setItemState) {
            SetItemState.NotStarted -> MaterialTheme.colorScheme.onSurfaceVariant
            SetItemState.InProgress -> MaterialTheme.colorScheme.onPrimary
            SetItemState.Completed -> MaterialTheme.colorScheme.primary
        },
        label = "Text color"
    )

    ListItem(
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, borderColor.value, PillShape)
                    .background(backgroundColor.value, PillShape)
            ) {
                Text(
                    text = (setIndex + 1).toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = textColor.value,
                    textAlign = TextAlign.Center,
                )
            }
        },
        title = {},
        modifier = modifier,
    )
}

@LightAndDarkThemePreview
@Composable
private fun SetItemPreview() {
    LiftAppTheme {
        val inProgressSetItemIndex = remember { mutableIntStateOf(1) }
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            repeat(5) { index ->
                SetItem(
                    setIndex = index,
                    setItemState = when {
                        inProgressSetItemIndex.intValue == index -> SetItemState.InProgress
                        inProgressSetItemIndex.intValue > index -> SetItemState.Completed
                        else -> SetItemState.NotStarted
                    },
                    modifier = Modifier.clickable { inProgressSetItemIndex.intValue = index }
                )

            }
        }
    }
}
