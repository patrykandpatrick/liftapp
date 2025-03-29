package com.patrykandpatryk.liftapp.feature.exercises.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.calculateStartPadding
import com.patrykandpatryk.liftapp.core.extension.getBottom
import com.patrykandpatryk.liftapp.core.extension.thenIf
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.model.valueOrNull
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.CheckableListItem
import com.patrykandpatryk.liftapp.core.ui.Error
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListItemDefaults
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.SearchBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.model.toLoadable
import com.patrykandpatryk.liftapp.feature.exercises.model.Action
import com.patrykandpatryk.liftapp.feature.exercises.model.GroupBy
import com.patrykandpatryk.liftapp.feature.exercises.model.ScreenState

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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (loadableScreenState.valueOrNull()?.pickingMode != true) {
                ExtendedFloatingActionButton(
                    text = stringResource(id = R.string.action_new_exercise),
                    icon = painterResource(id = R.drawable.ic_add),
                    onClick = { onAction(Action.GoToNewExercise) },
                )
            }
        },
        topBar = {
            loadableScreenState.Unfold(onError = null) { state ->
                TopBar(
                    state = state,
                    topAppBarScrollBehavior = topAppBarScrollBehavior,
                    onAction = onAction,
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
        loadableScreenState.Unfold(onError = { Error(Modifier.padding(internalPadding)) }) { state
            ->
            ListContent(
                state = state,
                onAction = onAction,
                paddingValues = internalPadding,
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
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding =
            PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = WindowInsets.navigationBars.getBottom(),
            ),
    ) {
        if (state.query.isEmpty()) {
            item {
                Controls(
                    groupBy = state.groupBy,
                    onGroupBySelection = { onAction(Action.SetGroupBy(it)) },
                )
            }
        }

        items(items = state.exercises, key = { it.key }, contentType = { it::class }) { item ->
            when (item) {
                is ExercisesItem.Exercise -> {
                    ExerciseItem(state = state, item = item, onAction = onAction)
                }

                is ExercisesItem.Header -> {
                    ListSectionTitle(
                        title = item.title,
                        modifier =
                            Modifier.animateItem()
                                .padding(
                                    start =
                                        MaterialTheme.dimens.list.itemIconBackgroundSize +
                                            ListItemDefaults.paddingValues.calculateStartPadding()
                                ),
                    )
                }
            }
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
        CheckableListItem(
            title = item.name,
            description = item.muscles,
            iconPainter = painterResource(id = item.iconRes),
            modifier =
                Modifier.padding(
                        horizontal = MaterialTheme.dimens.list.checkedItemHorizontalPadding
                    )
                    .animateItem(),
            checked = item.checked,
            onCheckedChange = { checked -> onAction(Action.SetExerciseChecked(item.id, checked)) },
            enabled = item.enabled,
        )
    } else {
        ListItem(
            title = item.name,
            description = item.muscles,
            iconPainter = painterResource(id = item.iconRes),
            modifier =
                Modifier.animateItem().clickable { onAction(Action.GoToExerciseDetails(item.id)) },
            enabled = item.enabled,
            titleHighlightPosition = item.nameHighlightPosition,
        )
    }
}

@Composable
private fun TopBar(
    state: ScreenState,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    onAction: (Action) -> Unit,
    navigateBack: () -> Unit,
) {
    if (state.pickingMode) {
        Column {
            TopAppBar(
                title = {
                    Text(
                        text =
                            stringResource(id = R.string.title_x_selected, state.selectedItemCount),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Start,
                    )
                },
                scrollBehavior = topAppBarScrollBehavior,
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(id = R.string.action_close),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
            )

            SearchBar(
                value = state.query,
                onValueChange = { onAction(Action.SetQuery(it)) },
                modifier = Modifier.padding(all = MaterialTheme.dimens.padding.contentHorizontal),
            )
        }
    } else {
        SearchBar(
            value = state.query,
            onValueChange = { onAction(Action.SetQuery(it)) },
            modifier =
                Modifier.statusBarsPadding()
                    .padding(all = MaterialTheme.dimens.padding.contentHorizontal),
        )
    }
}

@Composable
private fun BottomBar(mode: ExerciseListRouteData.Mode.Pick, onAction: (Action) -> Unit) {
    BottomAppBar(contentPadding = PaddingValues(start = 4.dp, end = 16.dp)) {
        IconButton(onClick = { onAction(Action.GoToNewExercise) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = stringResource(id = R.string.action_new_exercise),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        ExtendedFloatingActionButton(
            text = stringResource(id = R.string.action_done),
            icon = painterResource(id = R.drawable.ic_check),
            onClick = { onAction(Action.FinishPickingExercises(mode.resultKey)) },
            modifier = Modifier.align(Alignment.CenterVertically),
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        )
    }
}

@Composable
private fun Controls(groupBy: GroupBy, onGroupBySelection: (GroupBy) -> Unit) {
    Column(
        modifier =
            Modifier.padding(vertical = MaterialTheme.dimens.padding.exercisesControlsVertical)
    ) {
        Text(
            text = stringResource(id = R.string.generic_group_by),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = MaterialTheme.dimens.padding.contentHorizontal),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            modifier =
                Modifier.horizontalScroll(state = rememberScrollState())
                    .padding(horizontal = MaterialTheme.dimens.padding.contentHorizontal),
        ) {
            GroupBy.entries.forEach {
                val selected = groupBy == it
                FilterChip(
                    modifier = Modifier.animateContentSize(),
                    selected = selected,
                    onClick = { onGroupBySelection(it) },
                    leadingIcon = {
                        if (selected) {
                            Icon(
                                modifier = Modifier.size(LocalDimens.current.chip.iconSize),
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = null,
                                tint = LocalContentColor.current,
                            )
                        }
                    },
                    label = { Text(text = stringResource(id = it.labelResourceId)) },
                )
            }
        }
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
