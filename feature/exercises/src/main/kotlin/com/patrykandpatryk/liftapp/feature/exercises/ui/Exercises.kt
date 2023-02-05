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
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.collectInComposable
import com.patrykandpatryk.liftapp.core.extension.getBottom
import com.patrykandpatryk.liftapp.core.extension.thenIf
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.navigation.composable
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.provider.navigator
import com.patrykandpatryk.liftapp.core.provider.setResult
import com.patrykandpatryk.liftapp.core.ui.CheckableListItem
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.SearchBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.feature.exercises.model.Event
import com.patrykandpatryk.liftapp.feature.exercises.model.GroupBy
import com.patrykandpatryk.liftapp.feature.exercises.model.Intent
import com.patrykandpatryk.liftapp.feature.exercises.model.ScreenState

fun NavGraphBuilder.addExercises() {
    composable(
        route = Routes.Exercises,
    ) {
        Exercises()
    }
}

@Composable
fun Exercises(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
) {
    val viewModel: ExerciseViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val navigator = navigator

    viewModel.events.collectInComposable { event ->
        when (event) {
            is Event.OnExercisesPicked -> {
                navigator.setResult(
                    key = Constants.Keys.PICKED_EXERCISE_IDS,
                    result = event.exerciseIds,
                )
                navigator.popBackStack()
            }
        }
    }

    Exercises(
        modifier = modifier,
        padding = padding,
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@Composable
private fun Exercises(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    state: ScreenState,
    onIntent: (Intent) -> Unit,
) {

    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val navigator = navigator

    Scaffold(
        modifier = modifier.padding(bottom = padding.calculateBottomPadding()),
        floatingActionButton = {
            if (state.pickingMode.not()) {
                ExtendedFloatingActionButton(
                    text = stringResource(id = R.string.action_new_exercise),
                    icon = painterResource(id = R.drawable.ic_add),
                    onClick = { navigator.navigate(Routes.NewExercise.create()) },
                    modifier = Modifier
                        .thenIf(padding.calculateBottomPadding() == 0.dp) {
                            padding(WindowInsets.navigationBars.asPaddingValues())
                        },
                )
            }
        },
        topBar = {
            TopBar(
                state = state,
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                onIntent = onIntent,
                navigateBack = { navigator.popBackStack() },
            )
        },
        bottomBar = {
            if (state.pickingMode) {
                BottomBar(
                    onIntent = onIntent,
                )
            }
        },
        contentWindowInsets = WindowInsets.statusBars,
    ) { internalPadding ->

        LazyColumn(
            modifier = Modifier
                .thenIf(state.pickingMode) { nestedScroll(topAppBarScrollBehavior.nestedScrollConnection) },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(
                top = internalPadding.calculateTopPadding(),
                bottom = if (padding.calculateBottomPadding() == 0.dp) {
                    WindowInsets.navigationBars.getBottom()
                } else {
                    0.dp
                },
            ),
        ) {

            if (state.query.isEmpty()) {

                item {

                    Controls(
                        groupBy = state.groupBy,
                        onGroupBySelection = { onIntent(Intent.SetGroupBy(it)) },
                    )
                }
            }

            items(
                items = state.exercises,
                key = { it.key },
                contentType = { it::class },
            ) { item ->

                when (item) {
                    is ExercisesItem.Exercise -> {
                        ExerciseItem(
                            state = state,
                            item = item,
                            onIntent = onIntent,
                        )
                    }

                    is ExercisesItem.Header -> {
                        ListSectionTitle(
                            title = item.title,
                            modifier = Modifier
                                .animateItemPlacement()
                                .padding(start = 48.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LazyItemScope.ExerciseItem(
    state: ScreenState,
    item: ExercisesItem.Exercise,
    onIntent: (Intent) -> Unit,
) {
    val navigator = navigator

    if (state.pickingMode) {
        CheckableListItem(
            title = item.name,
            description = item.muscles,
            iconPainter = painterResource(id = item.iconRes),
            modifier = Modifier
                .padding(horizontal = MaterialTheme.dimens.list.checkedItemHorizontalPadding)
                .animateItemPlacement(),
            checked = item.checked,
            onCheckedChange = { checked ->
                onIntent(Intent.SetExerciseChecked(item.id, checked))
            },
            enabled = item.enabled,
        )
    } else {
        ListItem(
            title = item.name,
            description = item.muscles,
            iconPainter = painterResource(id = item.iconRes),
            modifier = Modifier
                .animateItemPlacement()
                .clickable { navigator.navigate(Routes.Exercise.create(item.id)) },
            enabled = item.enabled,
            titleHighlightPosition = item.nameHighlightPosition,
        )
    }
}

@Composable
private fun TopBar(
    state: ScreenState,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    onIntent: (Intent) -> Unit,
    navigateBack: () -> Unit,
) {
    if (state.pickingMode) {
        Column {

            TopAppBar(
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
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(id = R.string.action_close),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
            )

            SearchBar(
                value = state.query,
                onValueChange = { onIntent(Intent.SetQuery(it)) },
                modifier = Modifier
                    .padding(
                        all = MaterialTheme.dimens.padding.contentHorizontal,
                    ),
            )
        }
    } else {
        SearchBar(
            value = state.query,
            onValueChange = { onIntent(Intent.SetQuery(it)) },
            modifier = Modifier
                .statusBarsPadding()
                .padding(all = MaterialTheme.dimens.padding.contentHorizontal),
        )
    }
}

@Composable
private fun BottomBar(
    onIntent: (Intent) -> Unit,
) {
    val navigator = navigator
    BottomAppBar(
        contentPadding = PaddingValues(
            start = 4.dp,
            end = 16.dp,
        ),
    ) {

        IconButton(onClick = { navigator.navigate(Routes.NewExercise.create()) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = stringResource(id = R.string.action_new_exercise),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        ExtendedFloatingActionButton(
            text = stringResource(id = R.string.action_done),
            icon = painterResource(id = R.drawable.ic_check),
            onClick = { onIntent(Intent.FinishPickingExercises) },
            modifier = Modifier.align(Alignment.CenterVertically),
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        )
    }
}

@Composable
private fun Controls(
    groupBy: GroupBy,
    onGroupBySelection: (GroupBy) -> Unit,
) {

    Column(modifier = Modifier.padding(vertical = MaterialTheme.dimens.padding.exercisesControlsVertical)) {

        Text(
            text = stringResource(id = R.string.generic_group_by),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = MaterialTheme.dimens.padding.contentHorizontal),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            modifier = Modifier
                .horizontalScroll(state = rememberScrollState())
                .padding(horizontal = MaterialTheme.dimens.padding.contentHorizontal),
        ) {

            GroupBy.values().forEach {
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
        Exercises(
            state = getScreenState(pickingMode = false),
        ) {}
    }
}

@MultiDevicePreview
@Composable
fun ExercisesPreviewPickingMode() {
    LiftAppTheme {
        Exercises(
            state = getScreenState(pickingMode = true),
        ) {}
    }
}
