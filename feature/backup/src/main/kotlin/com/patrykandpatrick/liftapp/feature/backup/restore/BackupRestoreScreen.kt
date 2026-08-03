package com.patrykandpatrick.liftapp.feature.backup.restore

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatrick.liftapp.core.ui.BottomAppBar
import com.patrykandpatrick.liftapp.core.ui.TopAppBar
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.feature.backup.restore.model.Action
import com.patrykandpatrick.liftapp.feature.backup.restore.model.BackupRestoreState
import com.patrykandpatrick.liftapp.feature.backup.ui.DataTypeItem
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppErrorSnackbarHost
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.Archive
import com.patrykandpatrick.liftapp.ui.icons.Download
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons

@Composable
fun BackupRestoreScreen(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<BackupRestoreViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectSnackbarMessages(viewModel.messages, snackbarHostState)

    BackupRestoreScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun BackupRestoreScreen(
    state: BackupRestoreState,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.route_backup_import),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = { onAction(Action.PopBackStack) },
            )
        },
        bottomBar = {
            if (state is BackupRestoreState.Configuring) {
                BottomAppBar {
                    BottomAppBar.Button(
                        text = stringResource(R.string.backup_action_restore),
                        imageVector = LiftAppIcons.Download,
                        enabled = state.canRestore,
                        onClick = { onAction(Action.Restore) },
                    )
                }
            }
        },
        snackbarHost = { LiftAppErrorSnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (state) {
                BackupRestoreState.Reading -> Progress(null)
                is BackupRestoreState.Configuring -> Configuring(state, onAction)
                BackupRestoreState.Restoring ->
                    Progress(stringResource(R.string.backup_import_in_progress))
            }
        }
    }
}

@Composable
private fun Configuring(state: BackupRestoreState.Configuring, onAction: (Action) -> Unit) {
    val availableTypes = BackupDataType.entries.filter { it in state.available }
    Column(
        modifier =
            Modifier.verticalScroll(rememberScrollState())
                .padding(vertical = LocalDimens.current.screen.padding)
    ) {
        availableTypes.forEachIndexed { index, type ->
            DataTypeItem(
                type = type,
                checked = type in state.selected,
                nextItemSelected = availableTypes.getOrNull(index + 1) in state.selected,
                required = type in state.required,
                onCheckedChange = { onAction(Action.Toggle(type)) },
                position = LiftAppListItemPosition(index, availableTypes.size),
                modifier = Modifier.padding(horizontal = LocalDimens.current.screen.padding),
            )
        }
    }
}

@Composable
private fun Progress(message: String?) {
    EmptyState(
        icon = LiftAppIcons.Archive,
        message = message.orEmpty(),
        modifier = Modifier.fillMaxSize().padding(LocalDimens.current.screen.padding),
        actions = { CircularProgressIndicator() },
    )
}
