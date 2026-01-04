package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.prettyString
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.History
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.exercise.prettyString
import com.patrykandpatryk.liftapp.core.ui.ListItem

@Composable
internal fun SetItem(
    exercise: EditableWorkout.Exercise,
    set: EditableExerciseSet<*>,
    index: Int,
    onSelectSet: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val previousWorkoutSet = exercise.previousWorkoutSets.getOrNull(index)

    ListItem(
        modifier = modifier,
        title = {
            LiftAppText(
                text = set.prettyString(),
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        description = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier =
                    Modifier.padding(top = 4.dp)
                        .background(colorScheme.secondary, RoundedCornerShape(8.dp))
                        .padding(dimens.chip.horizontalPadding, 4.dp),
            ) {
                Icon(
                    imageVector = LiftAppIcons.History,
                    contentDescription = null,
                    tint = colorScheme.onSecondary,
                    modifier = Modifier.size(16.dp),
                )
                LiftAppText(
                    text = previousWorkoutSet.prettyString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSecondary,
                )
            }
        },
        onClick = { onSelectSet(index) },
    )
}
