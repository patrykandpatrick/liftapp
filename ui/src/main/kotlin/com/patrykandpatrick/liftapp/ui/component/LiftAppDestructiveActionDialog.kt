package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons

@Composable
fun LiftAppDestructiveActionDialog(
    title: String,
    text: String,
    confirmText: String,
    dismissText: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    LiftAppAlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icon(LiftAppIcons.Delete, null) },
        title = { Text(title) },
        text = { Text(text) },
        dismissButton = {
            LiftAppAlertDialogDefaults.DismissButton(
                onClick = onDismissRequest,
                text = dismissText,
            )
        },
        confirmButton = {
            PlainLiftAppButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
    )
}
