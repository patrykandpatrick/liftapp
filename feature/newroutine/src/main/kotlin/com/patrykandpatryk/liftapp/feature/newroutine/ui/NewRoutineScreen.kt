package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.theme.Colors.IllustrationAlpha
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.ErrorEffectState
import com.patrykandpatryk.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.animateJump
import com.patrykandpatryk.liftapp.core.ui.resource.iconRes
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.nonEmpty
import com.patrykandpatryk.liftapp.domain.validation.toInvalid
import com.patrykandpatryk.liftapp.domain.validation.toValid
import com.patrykandpatryk.liftapp.feature.newroutine.model.Action

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
    val windowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
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
                    IconButton(onClick = { onAction(Action.PopBackStack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_close),
                        )
                    }
                },
                actions = {
                    if (windowWidthSizeClass != WindowWidthSizeClass.COMPACT) {
                        TextButton(onClick = { onAction(Action.SaveRoutine(state)) }) {
                            Icon(painterResource(id = R.drawable.ic_save), null)
                            Spacer(
                                modifier = Modifier.width(LocalDimens.current.button.iconPadding)
                            )
                            Text(text = stringResource(id = R.string.action_save))
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
                BottomAppBar.Save(onClick = { onAction(Action.SaveRoutine(state)) })
            }
        },
    ) { paddingValues ->
        LazyColumn(modifier = modifier.padding(paddingValues)) {
            stickyHeader {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    LiftAppTextFieldWithSupportingText(
                        textFieldState = state.name,
                        label = { Text(text = stringResource(id = R.string.generic_name)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                            KeyboardActions(onDone = { onAction(Action.SaveRoutine(state)) }),
                        maxLines = LocalDimens.current.input.nameMaxLines,
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(horizontal = LocalDimens.current.padding.contentHorizontal),
                    )

                    ListSectionTitle(
                        title = stringResource(R.string.title_exercises),
                        trailingIcon = {
                            TextButton(
                                modifier =
                                    Modifier.animateJump(
                                        state.errorEffectState,
                                        state.exercises.isInvalid,
                                    ),
                                onClick = { onAction(Action.PickExercises(state.exerciseIds)) },
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_add_circle),
                                    contentDescription = null,
                                )
                                Spacer(
                                    modifier =
                                        Modifier.width(LocalDimens.current.button.iconPadding)
                                )
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

            exercises(state = state, onAction = onAction)
        }
    }
}

private fun LazyListScope.exercises(state: NewRoutineState, onAction: (Action) -> Unit) {
    if (state.exercises.isInvalid) {
        item { EmptyState(state) }
    } else {
        items(items = state.exercises.value, key = { it.id }, contentType = { it::class }) { item ->
            ListItem(
                modifier = Modifier.animateItem(),
                title = item.name,
                description = item.muscles,
                iconPainter = painterResource(id = item.type.iconRes),
                actions = {
                    IconButton(onClick = { onAction(Action.RemoveExercise(item.id)) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_remove_circle),
                            contentDescription = stringResource(id = R.string.list_remove),
                        )
                    }
                },
            )
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
            )
            .toValid(),
    )
}
