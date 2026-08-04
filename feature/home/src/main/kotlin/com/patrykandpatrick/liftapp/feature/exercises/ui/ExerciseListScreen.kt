package com.patrykandpatrick.liftapp.feature.exercises.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
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
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitleDefaults
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
import com.patrykandpatrick.liftapp.ui.component.LiftAppCheckbox
import com.patrykandpatrick.liftapp.ui.component.LiftAppChipRow
import com.patrykandpatrick.liftapp.ui.component.LiftAppFAB
import com.patrykandpatrick.liftapp.ui.component.LiftAppFilterChip
import com.patrykandpatrick.liftapp.ui.component.LiftAppFilterChipDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
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
import kotlin.math.roundToInt

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
    // The scaffold leaves one screen inset below the FAB; mirror it above the FAB.
    val scrollableContentBottomPadding = fabHeight + dimens.screen.padding * 2

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
    val segmentPositions =
        remember(state.exercises) { state.exercises.getExerciseSegmentPositions() }
    val firstSectionHeaderIndex =
        remember(state.exercises) { state.exercises.indexOfFirst { it is ExercisesItem.Header } }
    Box(modifier = modifier.imePadding()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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

            itemsIndexed(
                items = state.exercises,
                key = { _, item -> item.key },
                contentType = { _, item -> item::class },
            ) { index, item ->
                when (item) {
                    is ExercisesItem.Exercise -> {
                        ExerciseItem(
                            state = state,
                            item = item,
                            segmentPosition = segmentPositions[index],
                            nextItemSelected =
                                (state.exercises.getOrNull(index + 1) as? ExercisesItem.Exercise)
                                    ?.checked == true,
                            onAction = onAction,
                        )
                    }

                    is ExercisesItem.Header -> {
                        ListSectionTitle(
                            title = item.title,
                            spacing =
                                if (
                                    state.query.value.isEmpty() && index == firstSectionHeaderIndex
                                ) {
                                    ListSectionTitleDefaults.Spacing.AfterDivider
                                } else {
                                    ListSectionTitleDefaults.Spacing.Standard
                                },
                            modifier = Modifier.animateItem(),
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
                            start = dimens.screen.padding,
                            top = dimens.screen.padding,
                            end = dimens.screen.padding,
                            bottom = dimens.screen.padding,
                        ),
            )
        }
    }
}

@Composable
private fun LazyItemScope.ExerciseItem(
    state: ScreenState,
    item: ExercisesItem.Exercise,
    segmentPosition: SegmentPosition?,
    nextItemSelected: Boolean,
    onAction: (Action) -> Unit,
) {
    val position = checkNotNull(segmentPosition)
    val listItemPosition =
        LiftAppListItemPosition(
            index = position.index,
            count = position.count,
        )

    if (state.pickingMode) {
        LiftAppListItem(
            checked = item.checked,
            nextItemSelected = nextItemSelected,
            onCheckedChange = { onAction(Action.SetExerciseChecked(item.id, it)) },
            position = listItemPosition,
            modifier = Modifier.animateItem().padding(horizontal = dimens.screen.padding),
            enabled = item.enabled,
            icon = {
                LiftAppListItemDefaults.Icon {
                    Icon(imageVector = item.icon, contentDescription = null)
                }
            },
            actions = {
                LiftAppCheckbox(
                    checked = item.checked,
                    onCheckedChange = null,
                    enabled = item.enabled,
                )
            },
            description = { Text(item.muscles) },
            title = {
                LiftAppListItemDefaults.Title(
                    text = item.name,
                    highlightPosition = item.nameHighlightPosition,
                )
            },
        )
    } else {
        LiftAppListItem(
            onClick = { onAction(Action.GoToExerciseDetails(item.id)) },
            position = listItemPosition,
            modifier = Modifier.animateItem().padding(horizontal = dimens.screen.padding),
            enabled = item.enabled,
            icon = {
                LiftAppListItemDefaults.Icon {
                    Icon(imageVector = item.icon, contentDescription = null)
                }
            },
            description = { Text(item.muscles) },
            title = {
                LiftAppListItemDefaults.Title(
                    text = item.name,
                    highlightPosition = item.nameHighlightPosition,
                )
            },
        )
    }
}

private data class SegmentPosition(val index: Int, val count: Int)

private fun List<ExercisesItem>.getExerciseSegmentPositions(): List<SegmentPosition?> {
    val positions = MutableList<SegmentPosition?>(size) { null }
    var itemIndex = 0

    while (itemIndex < size) {
        if (this[itemIndex] !is ExercisesItem.Exercise) {
            itemIndex++
            continue
        }

        val segmentStart = itemIndex
        while (itemIndex < size && this[itemIndex] is ExercisesItem.Exercise) itemIndex++
        val segmentSize = itemIndex - segmentStart

        repeat(segmentSize) { index ->
            positions[segmentStart + index] = SegmentPosition(index = index, count = segmentSize)
        }
    }

    return positions
}

@Composable
private fun TopBar(
    state: ScreenState,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    navigateBack: () -> Unit,
) {
    val backgroundColor = colorScheme.background
    val searchBottomPadding = if (state.query.value.isEmpty()) 20.dp else 16.dp
    val gradient =
        Brush.verticalGradient(colors = listOf(backgroundColor, backgroundColor.copy(alpha = 0f)))

    if (state.mode is ExerciseListRouteData.Mode.Pick) {
        SelectionTopBar(
            selectedItemCount = state.selectedItemCount,
            searchBar = {
                val horizontalPadding = dimens.screen.padding
                SearchBar(
                    textFieldState = state.query,
                    modifier =
                        Modifier.padding(
                                start = horizontalPadding,
                                end = horizontalPadding,
                                bottom = searchBottomPadding,
                            )
                            .fractionalTopPadding(
                                maxPaddingPx =
                                    with(LocalDensity.current) { horizontalPadding.toPx() },
                                fraction = { topAppBarScrollBehavior.state.collapsedFraction },
                            ),
                )
            },
            backgroundColor = backgroundColor,
            scrollBehavior = topAppBarScrollBehavior,
            navigateBack = navigateBack,
        )
    } else {
        SearchBar(
            textFieldState = state.query,
            modifier =
                Modifier.background(gradient)
                    .statusBarsPadding()
                    .padding(
                        start = dimens.screen.padding,
                        top = dimens.screen.padding,
                        end = dimens.screen.padding,
                        bottom = searchBottomPadding,
                    ),
        )
    }
}

@Composable
private fun SelectionTopBar(
    selectedItemCount: Int,
    searchBar: @Composable () -> Unit,
    backgroundColor: Color,
    scrollBehavior: TopAppBarScrollBehavior,
    navigateBack: () -> Unit,
) {
    val headerHeight = TopAppBarDefaults.TopAppBarExpandedHeight
    val density = LocalDensity.current
    val collapseDistancePx = with(density) { headerHeight.toPx() }
    val headerTranslationDistancePx =
        with(density) { (headerHeight - dimens.screen.padding).toPx() }
    val contentScrolled by remember {
        derivedStateOf { scrollBehavior.state.overlappedFraction > 0.01f }
    }
    val scrolledChromeAlpha by
        animateFloatAsState(
            targetValue = if (contentScrolled) 1f else 0f,
            label = "selection header scrolled chrome",
        )
    val scrolledBackgroundColor = colorScheme.background
    val dividerColor = colorScheme.outline
    val dividerThickness = dimens.divider.thickness

    SideEffect { scrollBehavior.state.heightOffsetLimit = -collapseDistancePx }

    Column(
        modifier =
            Modifier.drawWithCache {
                    val gradient =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    backgroundColor,
                                    backgroundColor.copy(alpha = 0f),
                                ),
                            endY = size.height,
                        )
                    val dividerThicknessPx = dividerThickness.toPx()
                    onDrawBehind {
                        drawRect(brush = gradient)

                        val headerVisibleFraction =
                            1f - scrollBehavior.state.collapsedFraction.coerceIn(0f, 1f)
                        val chromeAlpha = scrolledChromeAlpha * headerVisibleFraction
                        drawRect(color = scrolledBackgroundColor, alpha = chromeAlpha)
                        drawLine(
                            color = dividerColor,
                            start = Offset(0f, size.height - dividerThicknessPx / 2f),
                            end = Offset(size.width, size.height - dividerThicknessPx / 2f),
                            strokeWidth = dividerThicknessPx,
                            alpha = chromeAlpha,
                        )
                    }
                }
                .statusBarsPadding()
                .collapseBy(
                    distancePx = collapseDistancePx,
                    fraction = { scrollBehavior.state.collapsedFraction },
                )
    ) {
        Box(
            modifier =
                Modifier.fillMaxWidth().height(headerHeight).graphicsLayer {
                    val collapsedFraction = scrollBehavior.state.collapsedFraction
                    alpha = 1f - collapsedFraction
                    translationY = -headerTranslationDistancePx * collapsedFraction
                }
        ) {
            LiftAppIconButton(
                onClick = navigateBack,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(
                    imageVector = LiftAppIcons.Cross,
                    contentDescription = stringResource(id = R.string.action_close),
                    tint = colorScheme.foreground,
                )
            }

            Text(
                text = stringResource(id = R.string.title_x_selected, selectedItemCount),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Box(
            modifier =
                Modifier.graphicsLayer {
                    translationY = -collapseDistancePx * scrollBehavior.state.collapsedFraction
                }
        ) {
            searchBar()
        }
    }
}

private fun Modifier.collapseBy(distancePx: Float, fraction: () -> Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val collapsedHeight = (distancePx * fraction().coerceIn(0f, 1f)).roundToInt()
        layout(placeable.width, (placeable.height - collapsedHeight).coerceAtLeast(0)) {
            placeable.placeRelative(0, 0)
        }
    }

private fun Modifier.fractionalTopPadding(
    maxPaddingPx: Float,
    fraction: () -> Float,
): Modifier = layout { measurable, constraints ->
    val topPadding = (maxPaddingPx * fraction().coerceIn(0f, 1f)).roundToInt()
    val placeable =
        measurable.measure(
            constraints.copy(
                minHeight = (constraints.minHeight - topPadding).coerceAtLeast(0),
                maxHeight = (constraints.maxHeight - topPadding).coerceAtLeast(0),
            )
        )
    layout(placeable.width, placeable.height + topPadding) {
        placeable.placeRelative(0, topPadding)
    }
}

@Composable
private fun BottomBar(mode: ExerciseListRouteData.Mode.Pick, onAction: (Action) -> Unit) {
    LiftAppBottomToolbar {
        Box(modifier = Modifier.fillMaxWidth().padding(dimens.screen.padding)) {
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
    Column {
        Text(
            text = stringResource(id = R.string.generic_group_by),
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.foregroundVariant,
            modifier = Modifier.padding(horizontal = dimens.screen.padding),
        )

        Spacer(Modifier.height(8.dp))

        LiftAppChipRow(
            modifier =
                Modifier.fillMaxWidth()
                    .horizontalScroll(state = rememberScrollState())
                    .padding(horizontal = dimens.screen.padding)
        ) {
            GroupBy.entries.forEach {
                val selected = groupBy == it
                LiftAppFilterChip(
                    selected = selected,
                    onClick = { onGroupBySelection(it) },
                    leadingIcon = { LiftAppFilterChipDefaults.Icon(vector = LiftAppIcons.Check) },
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

        Spacer(Modifier.height(20.dp))

        SinHorizontalDivider()
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
