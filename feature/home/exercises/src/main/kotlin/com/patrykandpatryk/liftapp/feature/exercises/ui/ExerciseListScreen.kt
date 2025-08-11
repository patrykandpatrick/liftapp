package com.patrykandpatryk.liftapp.feature.exercises.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
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
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.exception.getUIMessage
import com.patrykandpatryk.liftapp.core.extension.calculateStartPadding
import com.patrykandpatryk.liftapp.core.extension.thenIf
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.model.valueOrNull
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListItemDefaults
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.SearchBar
import com.patrykandpatryk.liftapp.core.ui.error.Error
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

    LiftAppScaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            LiftAppFAB(
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
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
        loadableScreenState.Unfold(
            onError = {
                Error(message = it.getUIMessage(), modifier = Modifier.padding(internalPadding))
            }
        ) { state ->
            ListContent(
                state = state,
                onAction = onAction,
                contentPadding = internalPadding,
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
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
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
                                        dimens.list.itemIconBackgroundSize +
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
        ListItem(
            title = item.name,
            description = item.muscles,
            iconPainter = painterResource(id = item.iconRes),
            modifier =
                Modifier.padding(horizontal = dimens.list.checkedItemHorizontalPadding)
                    .animateItem(),
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
    navigateBack: () -> Unit,
) {
    if (state.mode is ExerciseListRouteData.Mode.Pick) {
        Column {
            CompactTopAppBar(
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
                            tint = colorScheme.onSurface,
                        )
                    }
                },
            )

            SearchBar(
                textFieldState = state.query,
                modifier = Modifier.padding(all = dimens.padding.contentHorizontal),
            )
        }
    } else {
        SearchBar(
            textFieldState = state.query,
            modifier = Modifier.statusBarsPadding().padding(all = dimens.padding.contentHorizontal),
        )
    }
}

@Composable
private fun BottomBar(mode: ExerciseListRouteData.Mode.Pick, onAction: (Action) -> Unit) {
    BottomAppBar {
        LiftAppButton(
            onClick = { onAction(Action.FinishPickingExercises(mode.resultKey)) },
            modifier = Modifier.fillMaxWidth(),
            colors = LiftAppButtonDefaults.primaryButtonColors,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = stringResource(id = R.string.action_done),
            )
            Text(stringResource(id = R.string.action_done))
        }
    }
}

@Composable
private fun Controls(groupBy: GroupBy, onGroupBySelection: (GroupBy) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVertical),
        modifier = Modifier.padding(vertical = dimens.padding.exercisesControlsVertical),
    ) {
        Text(
            text = stringResource(id = R.string.generic_group_by),
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = dimens.padding.contentHorizontal),
        )

        LiftAppChipRow(
            modifier =
                Modifier.fillMaxWidth()
                    .horizontalScroll(state = rememberScrollState())
                    .padding(horizontal = dimens.padding.contentHorizontal)
        ) {
            GroupBy.entries.forEach {
                val selected = groupBy == it
                LiftAppFilterChip(
                    selected = selected,
                    onClick = { onGroupBySelection(it) },
                    leadingIcon = {
                        if (selected) {
                            LiftAppFilterChipDefaults.Icon(vector = LiftAppIcons.Check)
                        }
                    },
                    label = { Text(text = stringResource(id = it.labelResourceId)) },
                )
            }
        }

        SinHorizontalDivider(modifier = Modifier.padding(top = dimens.padding.itemVerticalSmall))
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
