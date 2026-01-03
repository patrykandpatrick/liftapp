package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.feature.workout.model.WorkoutIterator
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.appendBulletSeparator
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.modifier.topTintedEdge
import com.patrykandpatrick.liftapp.ui.theme.BottomSheetShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.getDisplayName

@Composable
internal fun BottomBar(
    nextIncompleteItem: WorkoutIterator.Item?,
    onGoToNextIncompleteItem: (WorkoutIterator.Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimens.verticalItemSpacing),
        modifier =
            modifier
                .topTintedEdge(BottomSheetShape)
                .dropShadow(BottomSheetShape) {
                    radius = 6.dp.toPx()
                    color = Color.Black.copy(alpha = .24f)
                }
                .dropShadow(BottomSheetShape) {
                    radius = 1.dp.toPx()
                    spread = 1.dp.toPx()
                    color = Color.Black.copy(alpha = .24f)
                }
                .pointerInput(Unit) {}
                .background(colorScheme.surface, BottomSheetShape)
                .fillMaxWidth()
                .padding(dimens.padding.contentHorizontal, dimens.padding.itemVertical)
                .padding(bottom = dimens.padding.itemVerticalSmall)
                .navigationBarsPadding(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontal),
        ) {
            CircularProgressIndicator(
                progress = { nextIncompleteItem?.progress ?: 1f },
                color = colorScheme.onSurface,
                trackColor = colorScheme.outline,
                modifier = Modifier.size(32.dp),
            )
            Column {
                LiftAppText(
                    text = stringResource(R.string.workout_action_next_exercise),
                    style = MaterialTheme.typography.titleSmall,
                )
                if (nextIncompleteItem != null) {
                    LiftAppText(
                        text =
                            buildAnnotatedString {
                                append(nextIncompleteItem.exercise.name.getDisplayName())
                                appendBulletSeparator()
                                append(
                                    stringResource(
                                        R.string.exercise_set_set_index,
                                        nextIncompleteItem.setIndex + 1,
                                    )
                                )
                                append("/${nextIncompleteItem.exercise.sets.size}")
                            },
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }

        if (nextIncompleteItem != null) {
            LiftAppButton(
                onClick = { onGoToNextIncompleteItem(nextIncompleteItem) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Record set")
            }
        } else {
            LiftAppButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.workout_action_summary))
            }
        }
    }
}
