package com.patrykandpatrick.liftapp.feature.exercises.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.exception.getUIMessage
import com.patrykandpatrick.liftapp.core.extension.getBottom
import com.patrykandpatrick.liftapp.core.extension.increaseBy
import com.patrykandpatrick.liftapp.core.extension.thenIf
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.model.valueOrNull
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.ListItem
import com.patrykandpatrick.liftapp.core.ui.ListItemDefaults
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.core.ui.SearchBar
import com.patrykandpatrick.liftapp.core.ui.error.Error
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.model.toLoadable
import com.patrykandpatrick.liftapp.feature.exercises.model.Action
import com.patrykandpatrick.liftapp.feature.exercises.model.GroupBy
import com.patrykandpatrick.liftapp.feature.exercises.model.ScreenState
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppBottomToolbar
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppChipRow
import com.patrykandpatrick.liftapp.ui.component.LiftAppFAB
import com.patrykandpatrick.liftapp.ui.component.LiftAppFilterChip
import com.patrykandpatrick.liftapp.ui.component.LiftAppFilterChipDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.SinHorizontalDivider
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Check
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plus
import com.patrykandpatrick.liftapp.ui.icons.Search
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun ExerciseListScreen(modifier: Modifier = Modifier) {
    val viewModel: ExerciseViewModel = hiltViewModel()
    val loadableScreenState by viewModel.state.collectAsStateWithLifecycle()

    ExerciseListScreen(
        modifier = modifier,
        loadableScreenState = loadableScreenState,
        onAction = viewModel::handleAction,
    )
}

@Composable
private fun ExerciseListScreen(
    modifier: Modifier = Modifier,
    loadableScreenState: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val fabHeight = 24.dp + dimens.fab.verticalPadding * 2
    // The scaffold leaves 16 dp below the FAB; use the standard screen padding above it.
    val scrollableContentBottomPadding = fabHeight + 16.dp + dimens.screen.verticalPadding

    LiftAppScaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            LiftAppFAB(
                content = {
                    Icon(
                        imageVector = LiftAppIcons.Plus,
                        contentDescription = stringResource(id = R.string.action_new_exercise),
                    )

                    Text(stringResource(R.string.action_new_exercise))
                },
                onClick = { onAction(Action.GoToNewExercise) },
            )
        },
        topBar = {
            loadableScreenState.Unfold(onError = null) { state ->
                TopBar(
                    state = state,
                    topAppBarScrollBehavior = topAppBarScrollBehavior,
                    navigateBack = { onAction(Action.PopBackStack) },
                )
            }
        },
        bottomBar = {
            val mode = loadableScreenState.valueOrNull()?.mode
            if (mode is ExerciseListRouteData.Mode.Pick) {
                BottomBar(mode = mode, onAction = onAction)
            }
        },
        contentWindowInsets = WindowInsets.statusBars,
    ) { internalPadding ->
        val fabContentBottomPadding =
            if (WindowInsets.ime.getBottom() > 0.dp) 0.dp else scrollableContentBottomPadding

        loadableScreenState.Unfold(
            onError = {
                Error(message = it.getUIMessage(), modifier = Modifier.padding(internalPadding))
            }
        ) { state ->
            ListContent(
                state = state,
                onAction = onAction,
                contentPadding = internalPadding.increaseBy(bottom = fabContentBottomPadding),
                emptyStatePadding = internalPadding,
                modifier =
                    Modifier.thenIf(state.pickingMode) {
                        nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                    },
            )
        }
    }
}

@Composable
private fun ListContent(
    state: ScreenState,
    onAction: (Action) -> Unit,
    contentPadding: PaddingValues,
    emptyStatePadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.imePadding()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = contentPadding,
        ) {
            if (state.query.value.isEmpty()) {
                item {
                    Controls(
                        groupBy = state.groupBy,
                        onGroupBySelection = { onAction(Action.SetGroupBy(it)) },
                    )
                }
            }

            items(
                items = state.exercises,
                key = { item -> item.key },
                contentType = { item -> item::class },
            ) { item ->
                when (item) {
                    is ExercisesItem.Exercise -> {
                        ExerciseItem(state = state, item = item, onAction = onAction)
                    }

                    is ExercisesItem.Header -> {
                        ListSectionTitle(
                            title = item.title,
                            modifier = Modifier.animateItem(),
                            paddingValues =
                                PaddingValues(
                                    start = ListItemDefaults.leadingContentStartPadding,
                                    top = 16.dp,
                                    end = dimens.screen.horizontalPadding,
                                    bottom = 16.dp,
                                ),
                        )
                    }
                }
            }
        }

        if (state.exercises.isEmpty()) {
            EmptyState(
                icon = LiftAppIcons.Search,
                message = stringResource(R.string.state_no_results),
                modifier =
                    Modifier.padding(emptyStatePadding)
                        .fillMaxSize()
                        .padding(
                            horizontal = dimens.screen.horizontalPadding,
                            vertical = dimens.screen.verticalPadding,
                        ),
            )
        }
    }
}

@Composable
private fun LazyItemScope.ExerciseItem(
    state: ScreenState,
    item: ExercisesItem.Exercise,
    onAction: (Action) -> Unit,
) {
    if (state.pickingMode) {
        ListItem(
            title = item.name,
            description = item.muscles,
            imageVector = item.icon,
            modifier = Modifier.animateItem(),
            checked = item.checked,
            onClick = { onAction(Action.SetExerciseChecked(item.id, !item.checked)) },
            enabled = item.enabled,
            actions = { ListItemDefaults.Checkbox(item.checked) },
            titleHighlightPosition = item.nameHighlightPosition,
        )
    } else {
        ListItem(
            title = item.name,
            description = item.muscles,
            imageVector = item.icon,
            modifier = Modifier.animateItem(),
            enabled = item.enabled,
            titleHighlightPosition = item.nameHighlightPosition,
            onClick = { onAction(Action.GoToExerciseDetails(item.id)) },
        )
    }
}

@Composable
private fun TopBar(
    state: ScreenState,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    navigateBack: () -> Unit,
) {
    if (state.mode is ExerciseListRouteData.Mode.Pick) {
        CompactTopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_x_selected, state.selectedItemCount),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Start,
                )
            },
            scrollBehavior = topAppBarScrollBehavior,
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = LiftAppIcons.Cross,
                        contentDescription = stringResource(id = R.string.action_close),
                        tint = colorScheme.onSurface,
                    )
                }
            },
            content = {
                SearchBar(
                    textFieldState = state.query,
                    modifier = Modifier.padding(all = dimens.screen.horizontalPadding),
                )
            },
        )
    } else {
        val background = colorScheme.background
        SearchBar(
            textFieldState = state.query,
            modifier =
                Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(background, background.copy(alpha = 0f))
                        )
                    )
                    .statusBarsPadding()
                    .padding(all = dimens.screen.horizontalPadding),
        )
    }
}

@Composable
private fun BottomBar(mode: ExerciseListRouteData.Mode.Pick, onAction: (Action) -> Unit) {
    LiftAppBottomToolbar {
        Box(modifier = Modifier.fillMaxWidth().padding(dimens.screen.horizontalPadding)) {
            LiftAppButton(
                onClick = { onAction(Action.FinishPickingExercises(mode.resultKey)) },
                modifier = Modifier.fillMaxWidth(),
                colors = LiftAppButtonDefaults.primaryButtonColors,
            ) {
                Icon(
                    imageVector = LiftAppIcons.Check,
                    contentDescription = stringResource(id = R.string.action_done),
                )
                Text(stringResource(id = R.string.action_done))
            }
        }
    }
}

@Composable
private fun Controls(groupBy: GroupBy, onGroupBySelection: (GroupBy) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        Text(
            text = stringResource(id = R.string.generic_group_by),
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = dimens.screen.horizontalPadding),
        )

        LiftAppChipRow(
            modifier =
                Modifier.fillMaxWidth()
                    .horizontalScroll(state = rememberScrollState())
                    .padding(horizontal = dimens.screen.horizontalPadding)
        ) {
            GroupBy.entries.forEach {
                val selected = groupBy == it
                LiftAppFilterChip(
                    selected = selected,
                    onClick = { onGroupBySelection(it) },
                    leadingIcon = {
                        LiftAppFilterChipDefaults.Icon(vector = LiftAppIcons.Check)
                    },
                    leadingIconVisible = selected,
                    label = {
                        Text(
                            text = stringResource(id = it.labelResourceId),
                            maxLines = 1,
                            softWrap = false,
                        )
                    },
                )
            }
        }

        SinHorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

@MultiDevicePreview
@Composable
fun ExercisesPreview() {
    LiftAppTheme {
        ExerciseListScreen(
            loadableScreenState =
                getScreenState(mode = ExerciseListRouteData.Mode.View).toLoadable()
        ) {}
    }
}

@MultiDevicePreview
@Composable
fun ExercisesPreviewPickingMode() {
    LiftAppTheme {
        ExerciseListScreen(
            loadableScreenState =
                getScreenState(mode = ExerciseListRouteData.Mode.Pick("")).toLoadable()
        ) {}
    }
}
