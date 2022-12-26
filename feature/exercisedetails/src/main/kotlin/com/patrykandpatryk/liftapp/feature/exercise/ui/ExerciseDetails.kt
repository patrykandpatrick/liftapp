package com.patrykandpatryk.liftapp.feature.exercise.ui

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.collectInComposable
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.navigation.composable
import com.patrykandpatryk.liftapp.core.provider.navigator
import com.patrykandpatryk.liftapp.core.tabItems
import com.patrykandpatryk.liftapp.core.ui.TopAppBarWithTabs
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.feature.exercise.model.Event
import com.patrykandpatryk.liftapp.feature.exercise.model.Intent
import com.patrykandpatryk.liftapp.feature.exercise.model.ScreenState
import com.patrykandpatryk.liftapp.feature.exercise.model.tabs
import kotlinx.coroutines.launch

fun NavGraphBuilder.addExerciseDetails() {
    composable(route = Routes.Exercise) {
        ExerciseDetails()
    }
}

@Composable
fun ExerciseDetails(
    modifier: Modifier = Modifier,
) {

    val viewModel: ExerciseViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val navigator = navigator

    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    ExerciseDetails(
        modifier = modifier,
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )

    viewModel.events.collectInComposable { event ->
        when (event) {
            Event.ExerciseNotFound -> navigator.popBackStack()
            is Event.EditExercise -> navigator.navigate(Routes.NewExercise.create(exerciseId = event.id))
        }
    }

    DeleteExerciseDialog(
        isVisible = state.showDeleteDialog,
        exerciseName = state.name,
        onDismissRequest = { viewModel.handleIntent(Intent.HideDeleteDialog) },
        onConfirm = { viewModel.handleIntent(Intent.Delete) },
    )
}

@Composable
private fun ExerciseDetails(
    modifier: Modifier = Modifier,
    state: ScreenState,
    onIntent: (Intent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {

    val navigator = navigator

    val pagerState = rememberPagerState()

    val tabs = remember { tabs }

    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            TopAppBarWithTabs(
                title = state.name,
                onBackClick = navigator::popBackStack,
                selectedTabIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                tabs = tabs.tabItems,
            )
        },
        bottomBar = {
            BottomAppBar {

                IconButton(onClick = { onIntent(Intent.ShowDeleteDialog) }) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = stringResource(id = R.string.action_delete),
                    )
                }

                IconButton(onClick = { onIntent(Intent.Edit) }) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = stringResource(id = R.string.action_edit),
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { paddingValues ->

        HorizontalPager(
            modifier = Modifier
                .fillMaxSize(),
            pageCount = tabs.size,
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
            title = { Text(text = stringResource(id = R.string.generic_delete_something, exerciseName)) },
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

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewExerciseDetails() {
    LiftAppTheme {
        ExerciseDetails(
            state = ScreenState.Populated(
                name = "Bicep Curl",
                showDeleteDialog = false,
            ),
            onIntent = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
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
