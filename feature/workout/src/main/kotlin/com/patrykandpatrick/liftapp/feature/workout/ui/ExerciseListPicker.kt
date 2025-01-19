package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.thenIf
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.ui.BackdropState
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.wheel.WheelPicker
import com.patrykandpatryk.liftapp.core.ui.wheel.WheelPickerState
import com.patrykandpatryk.liftapp.domain.math.subFraction
import kotlin.math.roundToInt

@Composable
fun ExerciseListPicker(
    workout: EditableWorkout,
    wheelPickerState: WheelPickerState,
    backdropState: BackdropState,
    modifier: Modifier = Modifier,
) {
    WheelPicker(
        highlight = {
            Box(
                Modifier.graphicsLayer { alpha = backdropState.offsetFraction }
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp),
                    )
            )
        },
        state = wheelPickerState,
        modifier =
            modifier.graphicsLayer {
                scaleX = lerp(1f, 0.9f, backdropState.offsetFraction)
                scaleY = scaleX
                translationY =
                    lerp(
                        -(size.height - wheelPickerState.maxItemHeight) / 2,
                        0f,
                        backdropState.offsetFraction,
                    )
            },
    ) {
        val leadingItemWidth = remember { mutableIntStateOf(0) }
        val density = LocalDensity.current.density

        workout.exercises.forEachIndexed { index, exercise ->
            val setsTextWidth = remember(exercise.sets) { mutableIntStateOf(0) }

            Item(
                leadingContent = {
                    ExerciseItemIndex(
                        index = index,
                        modifier = Modifier.leadingItemWidth(leadingItemWidth, density),
                    )
                },
                title = exercise.name.getDisplayName(),
                isSelected = index == wheelPickerState.currentItem,
                revealOffset = backdropState.offsetFraction,
                trailingContent = {
                    ExerciseItemTrailing(
                        exercise = exercise,
                        modifier =
                            Modifier.thenIf(setsTextWidth.intValue > 0) {
                                    width(
                                        ((setsTextWidth.intValue / density).dp *
                                            backdropState.offsetFraction.subFraction(0f, .5f))
                                    )
                                }
                                .onGloballyPositioned {
                                    if (setsTextWidth.intValue == 0)
                                        setsTextWidth.intValue = it.size.width
                                }
                                .graphicsLayer {
                                    alpha = backdropState.offsetFraction.subFraction(.5f, 1f)
                                },
                    )
                },
            )
        }

        Item(
            title = stringResource(R.string.workout_summary_title),
            isSelected = workout.exercises.size == wheelPickerState.currentItem,
            revealOffset = backdropState.offsetFraction,
            leadingContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_finish),
                    contentDescription = stringResource(R.string.workout_summary_title),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.leadingItemWidth(leadingItemWidth, density),
                )
            },
        )
    }
}

private fun Modifier.leadingItemWidth(leadingItemWidth: MutableIntState, density: Float): Modifier =
    onGloballyPositioned { layoutCoordinates ->
            leadingItemWidth.intValue =
                (layoutCoordinates.size.width / density)
                    .roundToInt()
                    .coerceAtLeast(leadingItemWidth.intValue)
        }
        .widthIn(min = leadingItemWidth.intValue.dp)

@Composable
private fun ExerciseItemIndex(index: Int, modifier: Modifier = Modifier) {
    Text(
        text = "${index + 1}",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

@Composable
private fun ExerciseItemTrailing(
    exercise: EditableWorkout.Exercise,
    modifier: Modifier = Modifier,
) {
    Text(
        text =
            LocalMarkupProcessor.current.toAnnotatedString(
                stringResource(
                    R.string.workout_exercise_list_set_format,
                    exercise.completedSetCount,
                    exercise.sets.size,
                    pluralStringResource(R.plurals.set_count, exercise.sets.size),
                )
            ),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
private fun Item(
    title: String,
    isSelected: Boolean,
    revealOffset: Float,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable RowScope.() -> Unit)? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemHorizontal),
        modifier =
            modifier
                .graphicsLayer {
                    if (isSelected) return@graphicsLayer
                    alpha = revealOffset
                }
                .fillMaxWidth()
                .padding(
                    LocalDimens.current.padding.itemHorizontal,
                    LocalDimens.current.padding.itemVertical,
                ),
    ) {
        leadingContent?.invoke(this)

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        trailingContent?.invoke(this)
    }
}
