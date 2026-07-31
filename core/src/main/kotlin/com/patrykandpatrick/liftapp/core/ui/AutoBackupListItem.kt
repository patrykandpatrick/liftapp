package com.patrykandpatrick.liftapp.core.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.ui.resource.prettyString
import com.patrykandpatrick.liftapp.domain.backup.AutoBackupSettings
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.RefreshCcwDot
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/** The shared entry point to automatic-backup settings. */
@Composable
fun AutoBackupListItem(settings: AutoBackupSettings?, onClick: () -> Unit) {
    ListItem(
        title = { Text(stringResource(R.string.backup_auto_enabled)) },
        description = settings?.let { value -> { AutoBackupSummary(value) } },
        icon = {
            ListItemDefaults.Icon {
                Icon(LiftAppIcons.RefreshCcwDot, contentDescription = null)
            }
        },
        onClick = onClick,
    )
}

@Composable
private fun AutoBackupSummary(settings: AutoBackupSettings) {
    when {
        !settings.enabled -> Text(stringResource(R.string.backup_auto_off))
        // On but pointed nowhere is the one state worth flagging: it cannot run.
        settings.destination == null ->
            Text(
                stringResource(R.string.backup_auto_no_destination),
                color = colorScheme.error,
            )
        else -> Text(settings.interval.prettyString())
    }
}
