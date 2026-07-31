package com.patrykandpatrick.liftapp.feature.backup.overview

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.ui.AutoBackupListItem
import com.patrykandpatrick.liftapp.core.ui.ListItem
import com.patrykandpatrick.liftapp.core.ui.TopAppBar
import com.patrykandpatrick.liftapp.domain.backup.AutoBackupSettings
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import com.patrykandpatrick.liftapp.feature.backup.overview.model.Action
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.icons.Download
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Upload

/** Any document may turn out to be a backup, so the picker is not narrowed by MIME type. */
private val OPENABLE_TYPES = arrayOf("*/*")

@Composable
fun BackupOverviewScreen(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<BackupOverviewViewModel>()
    val autoBackup by viewModel.autoBackup.collectAsStateWithLifecycle()

    BackupOverviewScreen(
        autoBackup = autoBackup,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun BackupOverviewScreen(
    autoBackup: AutoBackupSettings?,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val pickFileToRestore =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) onAction(Action.Restore(BackupLocation(uri.toString())))
        }

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.route_backup),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = { onAction(Action.PopBackStack) },
            )
        },
    ) { paddingValues ->
        // Scrollable even though it all fits, or the collapsing header has nothing to collapse on.
        LazyColumn(contentPadding = paddingValues, modifier = Modifier.fillMaxHeight()) {
            item {
                ListItem(
                    title = stringResource(R.string.backup_action_back_up),
                    imageVector = LiftAppIcons.Upload,
                    onClick = { onAction(Action.BackUp) },
                )
            }

            item {
                ListItem(
                    title = stringResource(R.string.backup_action_restore),
                    imageVector = LiftAppIcons.Download,
                    onClick = { pickFileToRestore.launch(OPENABLE_TYPES) },
                )
            }

            item {
                AutoBackupListItem(
                    settings = autoBackup,
                    onClick = { onAction(Action.Automatic) },
                )
            }
        }
    }
}
