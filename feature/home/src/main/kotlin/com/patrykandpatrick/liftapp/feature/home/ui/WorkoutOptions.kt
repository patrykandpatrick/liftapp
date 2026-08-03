package com.patrykandpatrick.liftapp.feature.home.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.ui.LiftAppModalBottomSheetWithTopAppBar
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialog
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialogDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemDefaults
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * What can be done with a workout that is already recorded, offered where one is long-pressed. The
 * dashboard and the journal show the same cards, so they offer the same options.
 */
@Composable
internal fun WorkoutOptionsModal(
    workout: Workout?,
    onDismissRequest: () -> Unit,
    onDeleteClick: (Workout) -> Unit,
) {
    if (workout != null) {
        LiftAppModalBottomSheetWithTopAppBar(
            onDismissRequest = onDismissRequest,
            containerColor = colorScheme.background,
        ) { dismiss ->
            Spacer(Modifier.height(8.dp))

            LiftAppListItem(
                title = { Text(stringResource(R.string.action_delete)) },
                icon = {
                    LiftAppListItemDefaults.Icon {
                        Icon(
                            imageVector = LiftAppIcons.Delete,
                            contentDescription = stringResource(R.string.action_delete),
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = dimens.screen.padding),
                onClick = {
                    dismiss()
                    onDeleteClick(workout)
                },
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/** Deleting a workout cannot be undone yet, so it is asked about first. */
@Composable
internal fun DeleteWorkoutDialog(
    workout: Workout?,
    onDismissRequest: () -> Unit,
    onConfirm: (Workout) -> Unit,
) {
    if (workout != null) {
        LiftAppAlertDialog(
            onDismissRequest = onDismissRequest,
            // Not the workout's name: it is the routine's, so naming it here would read as
            // deleting the routine.
            title = { Text(stringResource(R.string.workout_delete_title)) },
            text = { Text(stringResource(R.string.workout_delete_message)) },
            dismissButton = {
                LiftAppAlertDialogDefaults.DismissButton(
                    onClick = onDismissRequest,
                    text = stringResource(android.R.string.cancel),
                )
            },
            confirmButton = {
                PlainLiftAppButton(onClick = { onConfirm(workout) }) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            icon = { Icon(imageVector = LiftAppIcons.Delete, contentDescription = null) },
        )
    }
}
