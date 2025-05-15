package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.model.valueOrNull
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.tabItems
import com.patrykandpatryk.liftapp.core.ui.TopAppBarWithTabs
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.model.toLoadable
import com.patrykandpatryk.liftapp.feature.exercise.model.Action
import com.patrykandpatryk.liftapp.feature.exercise.model.ScreenState
import com.patrykandpatryk.liftapp.feature.exercise.model.tabs
import kotlinx.coroutines.launch

@Composable
fun ExerciseDetailsScreen(modifier: Modifier = Modifier) {
    val viewModel: ExerciseDetailsViewModel = hiltViewModel()

    val loadableState by viewModel.screenState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    ExerciseDetailsScreen(
        modifier = modifier,
        screenState = loadableState,
        onAction = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )

    loadableState.Unfold { state ->
        DeleteExerciseDialog(
            isVisible = state.showDeleteDialog,
            exerciseName = state.name,
            onDismissRequest = { viewModel.handleIntent(Action.HideDeleteDialog) },
            onConfirm = { viewModel.handleIntent(Action.Delete) },
        )
    }
}

@Composable
private fun ExerciseDetailsScreen(
    screenState: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val tabs = tabs
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            TopAppBarWithTabs(
                title = screenState.valueOrNull()?.name.orEmpty(),
                onBackClick = { onAction(Action.PopBackStack) },
                selectedTabIndex = pagerState.currentPage,
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
private fun DeleteExerciseDialog(
    isVisible: Boolean,
    exerciseName: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = R.string.generic_delete_something, exerciseName))
            },
            text = { Text(text = stringResource(id = R.string.exercise_delete_message)) },
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

@MultiDevicePreview
@Composable
fun PreviewExerciseDetails() {
    LiftAppTheme {
        ExerciseDetailsScreen(
            screenState =
                ScreenState(
                        name = "Bicep Curl",
                        showDeleteDialog = false,
                        primaryMuscles = emptyList(),
                        secondaryMuscles = emptyList(),
                        tertiaryMuscles = emptyList(),
                    )
                    .toLoadable(),
            onAction = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewDeleteExerciseDialog() {
    LiftAppTheme {
        DeleteExerciseDialog(
            isVisible = true,
            exerciseName = "Bicep Curl",
            onDismissRequest = {},
            onConfirm = {},
        )
    }
}
