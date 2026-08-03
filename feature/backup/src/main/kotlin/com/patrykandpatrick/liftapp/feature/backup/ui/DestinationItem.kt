package com.patrykandpatrick.liftapp.feature.backup.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.icons.Folder
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * The folder a backup goes to, shared by the back up and automatic backup screens.
 *
 * A missing [name] is shown in the error color: without a folder neither screen can do the one
 * thing it exists for, so it reads as something to fix rather than as a value.
 */
@Composable
fun DestinationItem(
    name: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = LiftAppIcons.Folder,
    position: LiftAppListItemPosition = LiftAppListItemPosition.Single,
) {
    LiftAppListItem(
        title = { Text(stringResource(R.string.backup_destination)) },
        description = { NoDestinationAwareText(name, enabled) },
        icon =
            if (icon == null) null
            else {
                { LiftAppListItemDefaults.Icon { Icon(icon, contentDescription = null) } }
            },
        enabled = enabled,
        position = position,
        onClick = onClick,
        modifier = modifier,
    )
}

/** [name], or the stand-in for having none. */
@Composable
private fun NoDestinationAwareText(name: String?, enabled: Boolean) {
    if (name != null) {
        Text(name)
    } else {
        Text(
            text = stringResource(R.string.backup_no_destination),
            // Only worth flagging while the row can act on it. A disabled row is asking for
            // nothing, so it keeps the ordinary description color rather than a faded red.
            color = if (enabled) colorScheme.error else Color.Unspecified,
        )
    }
}
