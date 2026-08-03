package com.patrykandpatrick.liftapp.feature.backup.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.extension.stringResourceId
import com.patrykandpatrick.liftapp.core.ui.resource.imageVector
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.ui.component.LiftAppCheckbox
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition

/**
 * One row of the data picker the back up and restore screens share.
 *
 * A [required] type stays checked and stops responding: it is only in the selection because
 * something else in it cannot be read back without it.
 */
@Composable
fun DataTypeItem(
    type: BackupDataType,
    checked: Boolean,
    required: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier,
    position: LiftAppListItemPosition = LiftAppListItemPosition.Single,
    nextItemSelected: Boolean = false,
) {
    LiftAppListItem(
        title = stringResource(type.stringResourceId),
        imageVector = type.imageVector,
        checked = checked,
        nextItemSelected = nextItemSelected,
        enabled = !required,
        actions = {
            LiftAppCheckbox(checked = checked, onCheckedChange = null, enabled = !required)
        },
        onCheckedChange = { if (!required) onCheckedChange() },
        position = position,
        modifier = modifier,
    )
}
