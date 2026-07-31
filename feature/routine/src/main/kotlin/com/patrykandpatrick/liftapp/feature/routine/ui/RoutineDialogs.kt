package com.patrykandpatrick.liftapp.feature.routine.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialog
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialogDefaults
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons

@Composable
internal fun DeleteRoutineDialog(
    routineName: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    LiftAppAlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icon(LiftAppIcons.Delete, null) },
        title = { Text(stringResource(R.string.generic_delete_something, routineName)) },
        text = { Text(stringResource(R.string.routine_delete_message)) },
        dismissButton = {
            LiftAppAlertDialogDefaults.DismissButton(
                onDismissRequest,
                stringResource(android.R.string.cancel),
            )
        },
        confirmButton = {
            PlainLiftAppButton(onClick = onConfirm) { Text(stringResource(R.string.action_delete)) }
        },
    )
}
