package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.ui.BackdropState
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.wheel.WheelPicker
import com.patrykandpatryk.liftapp.core.ui.wheel.WheelPickerState
import kotlin.math.abs
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
        val indexTextWidth = remember { mutableIntStateOf(0) }
        val density = LocalDensity.current.density

        workout.exercises.forEachIndexed { index, exercise ->
            val positionOffset = remember { mutableFloatStateOf(1f) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(LocalDimens.current.padding.itemHorizontal),
                modifier =
                    Modifier.onPositionChange { offset, viewPortOffset ->
                            positionOffset.floatValue = abs(offset)
                        }
                        .graphicsLayer {
                            alpha =
                                if (positionOffset.floatValue == 0f || !backdropState.isOpened) {
                                    1f
                                } else {
                                    backdropState.offsetFraction
                                }
                        }
                        .fillMaxWidth()
                        .padding(
                            LocalDimens.current.padding.itemHorizontal,
                            LocalDimens.current.padding.itemVertical,
                        ),
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier =
                        Modifier.onGloballyPositioned { layoutCoordinates ->
                                indexTextWidth.intValue =
                                    (layoutCoordinates.size.width / density)
                                        .roundToInt()
                                        .coerceAtLeast(indexTextWidth.intValue)
                            }
                            .widthIn(min = indexTextWidth.intValue.dp),
                )

                Text(
                    text = exercise.name.getDisplayName(),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

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
                )
            }
        }
    }
}
