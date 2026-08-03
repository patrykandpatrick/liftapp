package com.patrykandpatrick.liftapp.feature.routine.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.extension.toPaddingValues
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.model.valueOrNull
import com.patrykandpatrick.liftapp.core.text.updateValueBy
import com.patrykandpatrick.liftapp.core.ui.BottomAppBar
import com.patrykandpatrick.liftapp.core.ui.InputFieldLayout
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitleDefaults
import com.patrykandpatrick.liftapp.core.ui.TopAppBar
import com.patrykandpatrick.liftapp.core.ui.input.NumberInput
import com.patrykandpatrick.liftapp.core.ui.wheel.DurationPicker
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.feature.routine.model.SupersetEditorState
import com.patrykandpatrick.liftapp.feature.routine.model.getText
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppErrorSnackbarHost
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.CircleMinus
import com.patrykandpatrick.liftapp.ui.icons.DragHandle
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlin.time.Duration.Companion.milliseconds
import sh.calvin.reorderable.ReorderableColumn

@Composable
fun SupersetScreen(modifier: Modifier = Modifier, viewModel: SupersetViewModel = hiltViewModel()) {
    val loadableState = viewModel.state.collectAsStateWithLifecycle().value
    val state = loadableState.valueOrNull()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorText = state?.error?.getText()

    LaunchedEffect(errorText) {
        if (errorText == null) return@LaunchedEffect
        snackbarHostState.showSnackbar(errorText)
        viewModel.clearError()
    }

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.title_superset),
                onBackClick = viewModel::popBackStack,
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        bottomBar = { BottomAppBar.Save(onClick = { state?.also(viewModel::save) }) },
        snackbarHost = { LiftAppErrorSnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        loadableState.Unfold { supersetState ->
            SupersetEditor(
                state = supersetState,
                viewModel = viewModel,
                modifier =
                    Modifier.padding(
                            WindowInsets.ime.toPaddingValues(
                                additionalBottom = -paddingValues.calculateBottomPadding()
                            )
                        )
                        .padding(paddingValues),
            )
        }
    }
}

@Composable
private fun SupersetEditor(
    state: SupersetEditorState,
    viewModel: SupersetViewModel,
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current

    LazyColumn(
        modifier = modifier.fillMaxSize().background(colorScheme.background),
        contentPadding = PaddingValues(vertical = dimens.screen.padding),
    ) {
        item(key = "description") {
            Text(
                text = stringResource(R.string.superset_description),
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.foregroundVariant,
                modifier = Modifier.padding(horizontal = dimens.screen.padding),
            )
        }

        item(key = "sets") {
            val focusManager = LocalFocusManager.current
            NumberInput(
                textFieldState = state.sets,
                hint = stringResource(R.string.superset_sets),
                onPlusClick = { state.sets.updateValueBy(1) },
                onMinusClick = { state.sets.updateValueBy(-1) },
                keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) }),
                modifier =
                    Modifier.padding(
                        start = dimens.screen.padding,
                        top = 16.dp,
                        end = dimens.screen.padding,
                    ),
            )
        }

        item(key = "rest_time") {
            InputFieldLayout(
                isError = state.restTime.hasError,
                label = { Text(stringResource(R.string.superset_rest_time)) },
                modifier =
                    Modifier.padding(
                        start = dimens.screen.padding,
                        top = 16.dp,
                        end = dimens.screen.padding,
                    ),
            ) {
                DurationPicker(
                    duration = state.restTime.value.milliseconds,
                    onDurationChange = { state.restTime.updateValue(it.inWholeMilliseconds) },
                    includeHours = false,
                )
            }
        }

        item(key = "included_exercises_title") {
            // The button's own padding is invisible until it is pressed, so subtract it from the
            // row inset to place the visible button content 32 dp from the screen edge.
            val buttonPadding = LiftAppButtonDefaults.plainContentPadding
            val layoutDirection = LocalLayoutDirection.current
            val exerciseCount = state.includedExercises.size
            ListSectionTitle(
                title =
                    pluralStringResource(
                        R.plurals.exercise_count_with_number,
                        exerciseCount,
                        exerciseCount,
                    ),
                inset = ListSectionTitleDefaults.Inset.ListItemContent,
                spacing = ListSectionTitleDefaults.Spacing.Section,
                endPadding =
                    (32.dp - buttonPadding.calculateEndPadding(layoutDirection)).coerceAtLeast(
                        0.dp
                    ),
                trailingIcon = {
                    PlainLiftAppButton(onClick = viewModel::pickExercises) {
                        Text(stringResource(R.string.action_add_exercises))
                    }
                },
            )
        }

        item(key = "included_exercises") {
            ReorderableColumn(
                list = state.includedExercises,
                onSettle = viewModel::reorderExercises,
            ) { index, exercise, _ ->
                val interactionSource = remember { MutableInteractionSource() }

                ReorderableItem {
                    ExerciseRow(
                        exercise = exercise,
                        dragHandleModifier =
                            Modifier.draggableHandle(interactionSource = interactionSource),
                        onRemove = { viewModel.removeExercise(exercise) },
                        interactionSource = interactionSource,
                        position = LiftAppListItemPosition(index, state.includedExercises.size),
                        modifier =
                            Modifier.longPressDraggableHandle(interactionSource = interactionSource)
                                .padding(
                                    start = dimens.screen.padding,
                                    end = dimens.screen.padding,
                                ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseRow(
    exercise: RoutineExerciseItem,
    dragHandleModifier: Modifier,
    onRemove: () -> Unit,
    interactionSource: MutableInteractionSource,
    position: LiftAppListItemPosition,
    modifier: Modifier = Modifier,
) {
    LiftAppListItem(
        title = { Text(exercise.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        position = position,
        description = { Text(exercise.muscles) },
        icon = {
            Icon(
                imageVector = LiftAppIcons.DragHandle,
                contentDescription = stringResource(R.string.action_reorder_list),
                modifier = dragHandleModifier,
            )
        },
        actions = {
            LiftAppIconButton(onClick = onRemove) {
                Icon(
                    imageVector = LiftAppIcons.CircleMinus,
                    contentDescription = stringResource(R.string.list_remove),
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        contentPadding =
            PaddingValues(
                start = 16.dp,
                top = 10.dp,
                end = 8.dp,
                bottom = 10.dp,
            ),
        interactionSource = interactionSource,
        modifier = modifier,
    )
}
