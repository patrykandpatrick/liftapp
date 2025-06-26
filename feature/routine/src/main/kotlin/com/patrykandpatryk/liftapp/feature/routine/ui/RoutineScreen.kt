package com.patrykandpatryk.liftapp.feature.routine.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.model.valueOrNull
import com.patrykandpatryk.liftapp.core.tabItems
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.TopAppBarWithTabs
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.feature.routine.model.Action
import com.patrykandpatryk.liftapp.feature.routine.model.ScreenState
import com.patrykandpatryk.liftapp.feature.routine.model.tabs
import kotlinx.coroutines.launch

@Composable
fun RoutineScreen(modifier: Modifier = Modifier) {
    val viewModel: RoutineViewModel = hiltViewModel()

    val loadableState by viewModel.screenState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    RoutineScreen(
        modifier = modifier,
        loadableState = loadableState,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState,
    )

    loadableState.valueOrNull()?.also { state ->
        DeleteRoutineDialog(
            isVisible = state.showDeleteDialog,
            routineName = state.name,
            onDismissRequest = { viewModel.handleAction(Action.HideDeleteDialog) },
            onConfirm = { viewModel.handleAction(Action.Delete) },
        )
    }
}

@Composable
private fun RoutineScreen(
    modifier: Modifier = Modifier,
    loadableState: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val tabs = tabs
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBarWithTabs(
                title = loadableState.valueOrNull()?.name.orEmpty(),
                onBackClick = { onAction(Action.PopBackStack) },
                selectedTabIndex = { pagerState.currentPage },
                selectedTabOffset = { pagerState.currentPageOffsetFraction },
                onTabSelected = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
                tabs = tabs.tabItems,
            )
        },
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { onAction(Action.ShowDeleteDialog) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = stringResource(id = R.string.action_delete),
                    )
                }

                IconButton(onClick = { onAction(Action.Edit) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = stringResource(id = R.string.action_edit),
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                ExtendedFloatingActionButton(
                    text = stringResource(R.string.action_work_out),
                    icon = painterResource(R.drawable.ic_workout),
                    onClick = { onAction(Action.StartWorkout) },
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            contentPadding = paddingValues,
        ) { index ->
            tabs[index].content()
        }
    }
}

@Composable
private fun DeleteRoutineDialog(
    isVisible: Boolean,
    routineName: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = R.string.generic_delete_something, routineName))
            },
            text = { Text(text = stringResource(id = R.string.routine_delete_message)) },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = stringResource(id = R.string.action_delete))
                }
            },
        )
    }
}
