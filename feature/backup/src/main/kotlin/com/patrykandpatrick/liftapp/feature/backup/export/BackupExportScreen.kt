package com.patrykandpatrick.liftapp.feature.backup.export

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatrick.liftapp.core.ui.BottomAppBar
import com.patrykandpatrick.liftapp.core.ui.ListItemDefaults
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.core.ui.TopAppBar
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import com.patrykandpatrick.liftapp.feature.backup.export.model.Action
import com.patrykandpatrick.liftapp.feature.backup.export.model.BackupExportState
import com.patrykandpatrick.liftapp.feature.backup.ui.DataTypeItem
import com.patrykandpatrick.liftapp.feature.backup.ui.DestinationItem
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppErrorSnackbarHost
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.Archive
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Upload

@Composable
fun BackupExportScreen(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<BackupExportViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectSnackbarMessages(viewModel.messages, snackbarHostState)

    BackupExportScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun BackupExportScreen(
    state: BackupExportState,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.route_backup_export),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = { onAction(Action.PopBackStack) },
            )
        },
        bottomBar = {
            if (state is BackupExportState.Configuring) {
                BottomAppBar {
                    BottomAppBar.Button(
                        text = stringResource(R.string.backup_action_export),
                        imageVector = LiftAppIcons.Upload,
                        enabled = state.canExport,
                        onClick = { onAction(Action.Export) },
                    )
                }
            }
        },
        snackbarHost = { LiftAppErrorSnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (state) {
                is BackupExportState.Configuring -> Configuring(state, onAction)
                BackupExportState.Exporting ->
                    Progress(stringResource(R.string.backup_export_in_progress))
            }
        }
    }
}

@Composable
private fun Configuring(state: BackupExportState.Configuring, onAction: (Action) -> Unit) {
    val pickFolder =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) onAction(Action.SetDestination(BackupLocation(uri.toString())))
        }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        DestinationItem(
            name = state.destinationName,
            // The data rows below are checkable and inset themselves; match them so the text lines
            // up down the screen.
            horizontalVisualInset = ListItemDefaults.horizontalVisualInset,
            onClick = { pickFolder.launch(null) },
        )

        ListSectionTitle(title = stringResource(R.string.backup_section_data))

        BackupDataType.entries.forEach { type ->
            DataTypeItem(
                type = type,
                checked = type in state.selected,
                required = type in state.required,
                onCheckedChange = { onAction(Action.Toggle(type)) },
            )
        }
    }
}

@Composable
private fun Progress(message: String) {
    EmptyState(
        icon = LiftAppIcons.Archive,
        message = message,
        modifier = Modifier.fillMaxSize().padding(LocalDimens.current.screen.horizontalPadding),
        actions = { CircularProgressIndicator() },
    )
}
