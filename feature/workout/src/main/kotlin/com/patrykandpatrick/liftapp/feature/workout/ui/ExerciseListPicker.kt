package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.ui.BackdropState
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.wheel.WheelPicker
import com.patrykandpatryk.liftapp.core.ui.wheel.WheelPickerState
import kotlin.math.abs

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
        workout.exercises.forEach { exercise ->
            val positionOffset = remember { mutableFloatStateOf(1f) }
            Text(
                text = exercise.name.getDisplayName(),
                style = MaterialTheme.typography.titleLarge,
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
            )
        }
    }
}
