package com.patrykandpatryk.liftapp.feature.newroutine.ui

import android.content.res.Configuration
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.patrykandpatrick.vico.core.extension.orZero
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.collectInComposable
import com.patrykandpatryk.liftapp.core.extension.getMessageTextOrNull
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.navigation.composable
import com.patrykandpatryk.liftapp.core.provider.RegisterResultListener
import com.patrykandpatryk.liftapp.core.provider.navigator
import com.patrykandpatryk.liftapp.core.state.equivalentSnapshotPolicy
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.OutlinedTextField
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.theme.Colors.IllustrationAlpha
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.validation.toValid
import com.patrykandpatryk.liftapp.feature.newroutine.model.Event
import com.patrykandpatryk.liftapp.feature.newroutine.model.Intent
import com.patrykandpatryk.liftapp.feature.newroutine.model.ScreenState

fun NavGraphBuilder.addNewRoutine() {

    composable(route = Routes.NewRoutine) {
        NewRoutine()
    }
}

@Composable
fun NewRoutine(
    modifier: Modifier = Modifier,
) {

    val viewModel: NewRoutineViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val errorMessage by remember {
        derivedStateOf(
            equivalentSnapshotPolicy { new, previous -> if (state.showErrors) new == previous else true },
        ) { state.name.getMessageTextOrNull() ?: "" }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val navigator = navigator

    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    RegisterResultListener(key = Constants.Keys.PICKED_EXERCISE_IDS) { pickedExerciseIds: List<Long> ->
        viewModel.handleIntent(Intent.AddPickedExercises(pickedExerciseIds))
        clearResult()
    }

    NewRoutine(
        modifier = modifier,
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        nameErrorText = errorMessage,
    )

    viewModel.events.collectInComposable { event ->
        when (event) {
            Event.EntrySaved -> navigator.popBackStack()
            Event.RoutineNotFound -> navigator.popBackStack()
        }
    }
}

@Composable
private fun NewRoutine(
    modifier: Modifier = Modifier,
    state: ScreenState,
    onIntent: (Intent) -> Unit,
    snackbarHostState: SnackbarHostState,
    nameErrorText: String,
) {

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val density = LocalDensity.current.density
    val navigator = navigator
    var fabTopToParentBottom by remember { mutableStateOf(0.dp) }

    Scaffold(
        modifier = modifier
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .imePadding(),
        topBar = {

            val titleRes = remember(state.isEdit) {
                if (state.isEdit) R.string.title_edit_routine else R.string.title_new_routine
            }

            TopAppBar(
                title = stringResource(id = titleRes),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = navigator::popBackStack,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .consumeWindowInsets(insets = WindowInsets.navigationBars)
                    .onGloballyPositioned { coordinates ->
                        val parentBottom = coordinates.parentLayoutCoordinates?.size?.height.orZero
                        val fabTop = coordinates.positionInParent().y
                        fabTopToParentBottom = ((parentBottom - fabTop) / density).dp
                    },
                text = stringResource(id = R.string.action_save),
                icon = painterResource(id = R.drawable.ic_save),
                onClick = { onIntent(Intent.Save) },
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(
                bottom = fabTopToParentBottom + LocalDimens.current.verticalItemSpacing,
            ),
        ) {
            content(
                state = state,
                onIntent = onIntent,
                nameErrorText = nameErrorText,
            )
        }
    }
}

private fun LazyListScope.content(
    state: ScreenState,
    onIntent: (Intent) -> Unit,
    nameErrorText: String,
) {
    stickyHeader {

        Column(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.surface,
            ),
        ) {

            OutlinedTextField(
                value = state.name.value,
                onValueChange = { onIntent(Intent.UpdateName(it)) },
                label = { Text(text = stringResource(id = R.string.generic_name)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onIntent(Intent.Save) }),
                maxLines = LocalDimens.current.input.nameMaxLines,
                supportingText = nameErrorText,
                isError = state.showErrors,
                showSupportingText = state.showErrors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LocalDimens.current.padding.contentHorizontal),
            )

            ListSectionTitle(
                modifier = Modifier.padding(top = LocalDimens.current.verticalItemSpacing),
                title = stringResource(id = R.string.title_exercises),
            )
        }
    }

    if (state.exercises.isEmpty()) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                EmptyState(state)
            }
        }
    } else {
        items(
            items = state.exercises,
            key = { it.id },
            contentType = { it::class },
        ) { item ->
            ListItem(
                modifier = Modifier.animateItemPlacement(),
                title = item.name,
                description = item.muscles,
                iconPainter = painterResource(id = item.iconRes),
                actions = {
                    IconButton(onClick = { onIntent(Intent.RemovePickedExercise(item.id)) }) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_remove_circle,
                            ),
                            contentDescription = stringResource(id = R.string.list_remove),
                        )
                    }
                },
            )
        }
    }

    item(key = R.string.action_add_exercise) {

        val navigator = navigator

        Button(
            modifier = Modifier
                .animateItemPlacement()
                .fillMaxWidth()
                .padding(
                    top = LocalDimens.current.verticalItemSpacing,
                    start = LocalDimens.current.padding.contentHorizontal,
                    end = LocalDimens.current.padding.contentHorizontal,
                ),
            onClick = {
                navigator.navigate(
                    Routes.Exercises.create(
                        pickExercises = true,
                        disabledExerciseIds = state.exerciseIds,
                    ),
                )
            },
        ) {

            Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = null)

            Spacer(modifier = Modifier.width(LocalDimens.current.button.iconPadding))

            Text(text = stringResource(id = R.string.action_add_exercise))
        }
    }
}

@Composable
private fun ColumnScope.EmptyState(state: ScreenState) {

    val normalColor = MaterialTheme.colorScheme.onSurfaceVariant
    val errorColor = MaterialTheme.colorScheme.error

    val color = remember { Animatable(normalColor) }

    LaunchedEffect(key1 = state.showErrors) {
        if (state.showErrors) {
            color.animateTo(errorColor, tween())
        } else {
            color.animateTo(normalColor, tween())
        }
    }

    Icon(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(vertical = MaterialTheme.dimens.verticalItemSpacing)
            .size(128.dp),
        painter = painterResource(id = R.drawable.ic_weightlifter_down),
        contentDescription = null,
        tint = color.value.copy(alpha = IllustrationAlpha),
    )

    Text(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(bottom = MaterialTheme.dimens.verticalItemSpacing),
        text = stringResource(id = R.string.state_no_exercises),
        style = MaterialTheme.typography.bodyMedium,
        color = color.value,
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.TABLET)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.TABLET)
@Composable
fun NewRoutinePreview() {
    LiftAppTheme {
        NewRoutine(
            state = ScreenState(
                name = "Name".toValid(),
                id = 0,
            ),
            snackbarHostState = SnackbarHostState(),
            onIntent = {},
            nameErrorText = "",
        )
    }
}
