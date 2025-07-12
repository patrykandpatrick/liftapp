package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialog
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialogDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.DragHandle
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.Colors.IllustrationAlpha
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.core.ui.ErrorEffectState
import com.patrykandpatryk.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.animateJump
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.nonEmpty
import com.patrykandpatryk.liftapp.domain.validation.toInvalid
import com.patrykandpatryk.liftapp.domain.validation.toValid
import com.patrykandpatryk.liftapp.feature.newroutine.model.Action
import kotlinx.coroutines.suspendCancellableCoroutine
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun NewRoutineScreen(modifier: Modifier = Modifier) {
    val viewModel: NewRoutineViewModel = hiltViewModel()

    val state = viewModel.state.collectAsStateWithLifecycle().value

    state.Unfold(modifier) { state ->
        NewRoutineScreen(state = state, onAction = viewModel::onAction)
    }
}

@Composable
private fun NewRoutineScreen(
    state: NewRoutineState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAtLeastMediumWidth =
        currentWindowAdaptiveInfo()
            .windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isDeleteDialogVisible = rememberSaveable { mutableStateOf(false) }

    DeleteRoutineDialog(
        isVisible = isDeleteDialogVisible.value,
        routineName = state.name.value,
        onDismissRequest = { isDeleteDialogVisible.value = false },
        onConfirm = { onAction(Action.DeleteRoutine(state.id)) },
    )

    LiftAppScaffold(
        modifier = modifier,
        topBar = {
            CompactTopAppBar(
                title = {
                    Text(
                        text =
                            stringResource(
                                if (state.isEdit) R.string.title_edit_routine
                                else R.string.title_new_routine
                            )
                    )
                },
                navigationIcon = {
                    CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) }
                },
                actions = {
                    if (state.isEdit) {
                        LiftAppIconButton(onClick = { isDeleteDialogVisible.value = true }) {
                            Icon(
                                imageVector = LiftAppIcons.Delete,
                                contentDescription = stringResource(id = R.string.action_delete),
                            )
                        }
                    }

                    if (isAtLeastMediumWidth) {
                        PlainLiftAppButton(onClick = { onAction(Action.SaveRoutine(state)) }) {
                            Text(text = stringResource(id = R.string.action_save))
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (!isAtLeastMediumWidth) {
                BottomAppBar.Save(onClick = { onAction(Action.SaveRoutine(state)) })
            }
        },
    ) { paddingValues ->
        val lazyListState = rememberLazyListState()
        val reorderableLazyListState =
            rememberReorderableLazyListState(lazyListState) { from, to ->
                suspendCancellableCoroutine { continuation ->
                    onAction(Action.ReorderExercise(from.index - 1, to.index - 1, continuation))
                }
            }

        LazyColumn(
            state = lazyListState,
            modifier = modifier.padding(paddingValues).fillMaxSize(),
        ) {
            item {
                Column(modifier = Modifier.padding(top = dimens.padding.contentVertical)) {
                    LiftAppTextFieldWithSupportingText(
                        textFieldState = state.name,
                        label = { Text(text = stringResource(id = R.string.generic_name)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                            KeyboardActions(onDone = { onAction(Action.SaveRoutine(state)) }),
                        maxLines = dimens.input.nameMaxLines,
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(horizontal = LocalDimens.current.padding.contentHorizontal),
                    )

                    ListSectionTitle(
                        title = stringResource(R.string.title_exercises),
                        trailingIcon = {
                            PlainLiftAppButton(
                                modifier =
                                    Modifier.animateJump(
                                        state.errorEffectState,
                                        state.exercises.isInvalid,
                                    ),
                                onClick = { onAction(Action.PickExercises(state.exerciseIds)) },
                            ) {
                                Text(text = stringResource(id = R.string.action_add_exercise))
                            }
                        },
                        paddingValues =
                            PaddingValues(
                                vertical = LocalDimens.current.padding.itemVerticalSmall,
                                horizontal = LocalDimens.current.padding.contentHorizontal,
                            ),
                    )
                }
            }

            exercises(
                state = state,
                reorderableLazyListState = reorderableLazyListState,
                onAction = onAction,
            )
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
        LiftAppAlertDialog(
            onDismissRequest = onDismissRequest,
            icon = { Icon(LiftAppIcons.Delete, null) },
            title = {
                Text(text = stringResource(id = R.string.generic_delete_something, routineName))
            },
            text = { Text(text = stringResource(id = R.string.routine_delete_message)) },
            dismissButton = {
                LiftAppAlertDialogDefaults.DismissButton(
                    onDismissRequest,
                    stringResource(id = android.R.string.cancel),
                )
            },
            confirmButton = {
                PlainLiftAppButton(onClick = onConfirm) {
                    Text(text = stringResource(id = R.string.action_delete))
                }
            },
        )
    }
}

private fun LazyListScope.exercises(
    state: NewRoutineState,
    reorderableLazyListState: ReorderableLazyListState,
    onAction: (Action) -> Unit,
) {
    if (state.exercises.isInvalid) {
        item { EmptyState(state) }
    } else {
        items(items = state.exercises.value, key = { it.id }, contentType = { it::class }) { item ->
            ReorderableItem(state = reorderableLazyListState, key = item.id) { isDragging ->
                val interactionSource = remember { MutableInteractionSource() }
                ListItem(
                    modifier =
                        Modifier.background(colorScheme.background)
                            .longPressDraggableHandle(interactionSource = interactionSource),
                    icon = {
                        Icon(
                            imageVector = LiftAppIcons.DragHandle,
                            contentDescription = null,
                            modifier =
                                Modifier.draggableHandle(interactionSource = interactionSource),
                        )
                    },
                    title = {
                        Text(text = item.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    description = { Text(text = item.muscles) },
                    actions = {
                        LiftAppIconButton(onClick = { onAction(Action.RemoveExercise(item.id)) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_remove_circle),
                                contentDescription = stringResource(id = R.string.list_remove),
                            )
                        }
                    },
                    interactionSource = interactionSource,
                )
            }
        }
    }
}

@Composable
private fun EmptyState(state: NewRoutineState, modifier: Modifier = Modifier) {
    val normalColor = MaterialTheme.colorScheme.onSurfaceVariant
    val errorColor = MaterialTheme.colorScheme.error

    val showErrors = state.showErrors
    val color = animateColorAsState(if (showErrors) errorColor else normalColor, label = "color")

    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.verticalItemSpacing),
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(
            modifier = Modifier.align(Alignment.CenterHorizontally).size(128.dp),
            painter = painterResource(id = R.drawable.ic_weightlifter_down),
            contentDescription = null,
            tint = color.value.copy(alpha = IllustrationAlpha),
        )

        Text(
            text = stringResource(id = R.string.state_no_exercises),
            style = MaterialTheme.typography.bodyMedium,
            color = color.value,
            modifier =
                Modifier.align(Alignment.CenterHorizontally)
                    .padding(bottom = MaterialTheme.dimens.verticalItemSpacing),
        )
    }
}

@Composable
private fun NewRoutinePreviewInternal(
    routineID: Long,
    routineName: String,
    exercises: Validatable<List<RoutineExerciseItem>>,
) {
    LiftAppTheme {
        val savedStateHandle = remember { SavedStateHandle() }
        val textFieldStateManager =
            TextFieldStateManager(
                stringProvider = PreviewResource.stringProvider,
                formatter = PreviewResource.formatter(),
                savedStateHandle = savedStateHandle,
            )
        NewRoutineScreen(
            state =
                NewRoutineState(
                    id = routineID,
                    routineName = "",
                    name =
                        textFieldStateManager.stringTextField(
                            initialValue = routineName,
                            validators = { nonEmpty() },
                        ),
                    exercises = exercises,
                    isEdit = false,
                    errorEffectState = ErrorEffectState(),
                    showErrors = false,
                ),
            onAction = {},
        )
    }
}

@MultiDevicePreview
@Composable
private fun NewRoutinePreview() {
    NewRoutinePreviewInternal(ID_NOT_SET, "", emptyList<RoutineExerciseItem>().toInvalid())
}

@MultiDevicePreview
@Composable
private fun EditRoutinePreview() {
    NewRoutinePreviewInternal(
        1,
        "Sample 1",
        listOf<RoutineExerciseItem>(
                RoutineExerciseItem(1, "Bench Press", "Chest", ExerciseType.Weight, Goal.default),
                RoutineExerciseItem(
                    2,
                    "Squats",
                    "Glutes, Quadriceps",
                    ExerciseType.Weight,
                    Goal.default,
                ),
                RoutineExerciseItem(
                    3,
                    "Deadlift",
                    "Hamstrings, Glutes",
                    ExerciseType.Weight,
                    Goal.default,
                ),
                RoutineExerciseItem(
                    4,
                    "Seated Overhead Dumbbell Press The Longest Name Ever",
                    "Shoulders, Chest",
                    ExerciseType.Weight,
                    Goal.default,
                ),
            )
            .toValid(),
    )
}
