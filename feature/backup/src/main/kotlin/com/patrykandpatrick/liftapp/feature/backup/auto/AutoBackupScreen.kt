package com.patrykandpatrick.liftapp.feature.backup.auto

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.ui.DropdownMenu
import com.patrykandpatrick.liftapp.core.ui.TopAppBar
import com.patrykandpatrick.liftapp.core.ui.resource.prettyString
import com.patrykandpatrick.liftapp.domain.backup.BackupInterval
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import com.patrykandpatrick.liftapp.domain.backup.BackupRetention
import com.patrykandpatrick.liftapp.feature.backup.ui.DestinationItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.LiftAppSwitchDefaults
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.CalendarDays
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons

@Composable
fun AutoBackupScreen(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<AutoBackupViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    AutoBackupScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun AutoBackupScreen(
    state: AutoBackupState?,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    var intervalDialogVisible by remember { mutableStateOf(false) }
    var retentionDialogVisible by remember { mutableStateOf(false) }
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val pickFolder =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) onAction(Action.SetDestination(BackupLocation(uri.toString())))
        }

    // A scheduled backup that fails has nothing but a notification to say so, and the answer does
    // not change whether it is turned on — hence the result being ignored.
    val requestNotifications =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.route_backup_auto),
                onBackClick = { onAction(Action.PopBackStack) },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) { paddingValues ->
        if (state == null) return@LiftAppScaffold

        val settings = state.settings
        val listItemModifier = Modifier.padding(horizontal = dimens.screen.padding)

        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = dimens.screen.padding)
        ) {
            LiftAppListItem(
                title = { Text(stringResource(R.string.backup_auto_turn_on)) },
                checked = settings.enabled,
                role = Role.Switch,
                actions = {
                    Switch(
                        checked = settings.enabled,
                        onCheckedChange = null,
                        colors = LiftAppSwitchDefaults.colors(),
                    )
                },
                modifier = listItemModifier,
                contentPadding = PaddingValues(16.dp),
                onCheckedChange = { enabled ->
                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    onAction(Action.SetEnabled(enabled))
                },
            )

            Spacer(Modifier.height(16.dp))

            Column {
                DestinationItem(
                    name = state.destinationName,
                    enabled = settings.enabled,
                    onClick = { pickFolder.launch(null) },
                    modifier = listItemModifier,
                    position = LiftAppListItemPosition(index = 0, count = 3),
                )

                LiftAppListItem(
                    title = stringResource(R.string.backup_auto_interval),
                    description = settings.interval.prettyString(),
                    imageVector = LiftAppIcons.CalendarDays,
                    enabled = settings.enabled,
                    modifier = listItemModifier,
                    position = LiftAppListItemPosition(index = 1, count = 3),
                    onClick = { intervalDialogVisible = true },
                )

                LiftAppListItem(
                    title = stringResource(R.string.backup_auto_retention),
                    description = settings.retention.prettyString(),
                    imageVector = LiftAppIcons.Delete,
                    enabled = settings.enabled,
                    modifier = listItemModifier,
                    position = LiftAppListItemPosition(index = 2, count = 3),
                    onClick = { retentionDialogVisible = true },
                )
            }
        }

        if (intervalDialogVisible) {
            OptionBottomSheet(
                title = stringResource(R.string.backup_auto_interval),
                options = BackupInterval.entries,
                selected = settings.interval,
                label = { it.prettyString() },
                onSelect = { onAction(Action.SetInterval(it)) },
                onDismissRequest = { intervalDialogVisible = false },
            )
        }

        if (retentionDialogVisible) {
            OptionBottomSheet(
                title = stringResource(R.string.backup_auto_retention),
                options = BackupRetention.entries,
                selected = settings.retention,
                label = { it.prettyString() },
                onSelect = { onAction(Action.SetRetention(it)) },
                onDismissRequest = { retentionDialogVisible = false },
            )
        }
    }
}

@Composable
private fun <T> OptionBottomSheet(
    title: String,
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(
        expanded = true,
        setExpanded = { if (!it) onDismissRequest() },
        selectedItems = listOf(selected),
        items = options,
        getItemText = label,
        modalTitle = title,
        onClick = onSelect,
        isMultiSelect = false,
        content = { _, _ -> },
    )
}
