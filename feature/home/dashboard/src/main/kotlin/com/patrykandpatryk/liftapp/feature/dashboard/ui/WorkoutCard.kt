package com.patrykandpatryk.liftapp.feature.dashboard.ui

import android.R.attr.top
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.vico.core.extension.forEachIndexedExtended
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.ui.RoutineCardShape
import com.patrykandpatryk.liftapp.domain.workout.Workout
import java.time.format.DateTimeFormatter

@Composable
fun WorkoutCard(workout: Workout, onClick: (Workout) -> Unit, modifier: Modifier = Modifier) {
    val dimens = LocalDimens.current
    val markupProcessor = LocalMarkupProcessor.current
    val datePattern = stringResource(R.string.dashboard_workout_date_pattern)
    val dateFormat = remember(datePattern) { DateTimeFormatter.ofPattern(datePattern) }
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoutineCardShape,
        onClick = { onClick(workout) },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier =
                Modifier.padding(
                    horizontal = dimens.padding.itemHorizontal,
                    vertical = dimens.padding.itemVertical,
                ),
        ) {
            Text(
                text =
                    markupProcessor.toAnnotatedString(
                        workout.endDate?.let { date ->
                            stringResource(
                                R.string.dashboard_completed_workout_date_title,
                                date.format(dateFormat),
                            )
                        }
                            ?: stringResource(
                                R.string.dashboard_active_workout_date_title,
                                workout.startDate.format(dateFormat),
                            )
                    ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = workout.name, style = MaterialTheme.typography.headlineSmall)

            Text(
                text =
                    buildAnnotatedString {
                        workout.exercises.forEachIndexedExtended { _, _, isLast, exercise ->
                            append("• ")
                            append(exercise.name.getDisplayName())
                            append(" • ")
                            append(
                                markupProcessor.toAnnotatedString(
                                    stringResource(
                                        R.string.workout_exercise_list_set_format,
                                        exercise.completedSets,
                                        exercise.totalSets,
                                        pluralStringResource(
                                            R.plurals.set_count,
                                            exercise.totalSets,
                                        ),
                                    )
                                )
                            )
                            if (!isLast) append("\n")
                        }
                    },
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
