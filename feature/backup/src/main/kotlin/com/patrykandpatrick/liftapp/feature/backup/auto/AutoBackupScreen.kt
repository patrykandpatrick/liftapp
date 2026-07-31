package com.patrykandpatrick.liftapp.feature.backup.auto

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatrick.liftapp.core.ui.DialogContent
import com.patrykandpatrick.liftapp.core.ui.ListItem
import com.patrykandpatrick.liftapp.core.ui.ListItemDefaults
import com.patrykandpatrick.liftapp.core.ui.dialog.DialogButtons
import com.patrykandpatrick.liftapp.core.ui.resource.prettyString
import com.patrykandpatrick.liftapp.domain.backup.BackupInterval
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import com.patrykandpatrick.liftapp.domain.backup.BackupRetention
import com.patrykandpatrick.liftapp.feature.backup.ui.DestinationItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.LiftAppSwitchDefaults
import com.patrykandpatrick.liftapp.ui.component.SinHorizontalDivider
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.graphics.rememberBottomSinShape
import com.patrykandpatrick.liftapp.ui.icons.CalendarDays
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

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

    val pickFolder =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) onAction(Action.SetDestination(BackupLocation(uri.toString())))
        }

    // A scheduled backup that fails has nothing but a notification to say so, and the answer does
    // not change whether it is turned on — hence the result being ignored.
    val requestNotifications =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    LiftAppScaffold(
        modifier = modifier,
        topBar = {
            CompactTopAppBar(
                title = {
                    CompactTopAppBarDefaults.Title(stringResource(R.string.route_backup_auto))
                },
                navigationIcon = {
                    CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) }
                },
                // The header panel below continues this surface and supplies the lower edge.
                alwaysShowChrome = true,
                divider = false,
            )
        },
    ) { paddingValues ->
        if (state == null) return@LiftAppScaffold

        val settings = state.settings

        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
        ) {
            TurnOnHeader(
                enabled = settings.enabled,
                onCheckedChange = { enabled ->
                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    onAction(Action.SetEnabled(enabled))
                },
            )

            DestinationItem(
                name = state.destinationName,
                enabled = settings.enabled,
                onClick = { pickFolder.launch(null) },
            )

            ListItem(
                title = stringResource(R.string.backup_auto_interval),
                description = settings.interval.prettyString(),
                imageVector = LiftAppIcons.CalendarDays,
                enabled = settings.enabled,
                onClick = { intervalDialogVisible = true },
            )

            ListItem(
                title = stringResource(R.string.backup_auto_retention),
                description = settings.retention.prettyString(),
                imageVector = LiftAppIcons.Delete,
                enabled = settings.enabled,
                onClick = { retentionDialogVisible = true },
            )
        }

        if (intervalDialogVisible) {
            OptionDialog(
                title = stringResource(R.string.backup_auto_interval),
                options = BackupInterval.entries,
                selected = settings.interval,
                label = { it.prettyString() },
                onSelect = { onAction(Action.SetInterval(it)) },
                onDismissRequest = { intervalDialogVisible = false },
            )
        }

        if (retentionDialogVisible) {
            OptionDialog(
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

/**
 * Carries the one switch the rest of the screen depends on, styled like the Plan tab's header: a
 * surface panel whose bottom edge is cut by the sin curve the divider then traces.
 */
@Composable
private fun TurnOnHeader(
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The gap a selected list item leaves between its border and the screen edge, reused here to
    // keep the wave off both the row above it and the list below.
    val breathingRoom = ListItemDefaults.horizontalVisualInset

    Box(modifier = modifier.padding(bottom = breathingRoom)) {
        Box(
            // Painted rather than clipped. A clip lays down a layer, and a layer both hit-tests
            // taps against its shape — which stopped them reaching the row — and scales along with
            // whatever it contains. The panel and its edge belong to the header and stay put.
            modifier =
                Modifier.background(
                        color = colorScheme.surface,
                        shape = rememberBottomSinShape(),
                    )
                    .fillMaxWidth()
                    .padding(bottom = dimens.divider.sinHeight + breathingRoom)
        ) {
            Row(
                // The row alone takes the press, and takes it the way the rows below do: the same
                // scale and the same border, running the full width as theirs does.
                modifier =
                    Modifier.interactiveButtonEffect(
                            colors =
                                ListItemDefaults.colors
                                    .getColors(checked = false)
                                    .interactiveBorderColors,
                            onClick = { onCheckedChange(!enabled) },
                            shape = MaterialTheme.shapes.medium,
                            role = Role.Switch,
                        )
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimens.screen.horizontalPadding,
                            vertical = 16.dp,
                        ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.backup_auto_turn_on),
                    style = typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )

                Switch(
                    checked = enabled,
                    onCheckedChange = null,
                    colors = LiftAppSwitchDefaults.colors(),
                )
            }
        }

        SinHorizontalDivider(
            color = colorScheme.divider,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun <T> OptionDialog(
    title: String,
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        DialogContent(
            title = title,
            actions = { DialogButtons(onNegativeButtonClick = onDismissRequest) },
        ) {
            options.forEach { option ->
                OptionRow(
                    title = label(option),
                    selected = option == selected,
                    onClick = {
                        onSelect(option)
                        onDismissRequest()
                    },
                )
            }
        }
    }
}

/** Laid out as the option rows in Settings are, so the two dialogs read as the same control. */
@Composable
private fun OptionRow(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 12.dp),
    ) {
        RadioButton(selected = selected, onClick = onClick)

        Text(text = title)
    }
}
