package com.patrykandpatrick.feature.exercisegoal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.feature.exercisegoal.model.Action
import com.patrykandpatrick.feature.exercisegoal.model.GetExerciseNameAndTypeUseCase
import com.patrykandpatrick.feature.exercisegoal.model.GetGoalUseCase
import com.patrykandpatrick.feature.exercisegoal.model.GoalInput
import com.patrykandpatrick.feature.exercisegoal.model.SaveGoalUseCase
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.component.SinHorizontalDivider
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.interfaceStub
import com.patrykandpatryk.liftapp.core.extension.prettyString
import com.patrykandpatryk.liftapp.core.extension.thenIf
import com.patrykandpatryk.liftapp.core.extension.toPaddingValues
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.text.updateValueBy
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.Info
import com.patrykandpatryk.liftapp.core.ui.InfoDefaults
import com.patrykandpatryk.liftapp.core.ui.InputFieldLayout
import com.patrykandpatryk.liftapp.core.ui.input.NumberInput
import com.patrykandpatryk.liftapp.core.ui.wheel.DurationPicker
import com.patrykandpatryk.liftapp.domain.Constants.Input.Increment
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseNameAndType
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.goal.SaveGoalContract
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf

@Composable
fun ExerciseGoalScreen(
    modifier: Modifier = Modifier,
    viewModel: ExerciseGoalViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val dimens = LocalDimens.current

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = state?.exerciseName?.getDisplayName().orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(Action.PopBackStack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_close),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.onAction(
                                Action.SetGoalInfoVisible(state?.goalInfoVisible != true)
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Info,
                            contentDescription = stringResource(id = R.string.action_info),
                        )
                    }
                },
            )
        },
        bottomBar = {
            if (state != null) {
                BottomAppBar.Save(onClick = { viewModel.onAction(Action.SaveGoal(state)) })
            }
        },
    ) { paddingValues ->
        LazyVerticalGrid(
            modifier =
                modifier
                    .padding(
                        WindowInsets.ime.toPaddingValues(
                            additionalBottom = -paddingValues.calculateBottomPadding()
                        )
                    )
                    .padding(paddingValues)
                    .fillMaxSize(),
            columns = GridCells.Adaptive(minSize = dimens.grid.minCellWidthLarge),
            contentPadding =
                PaddingValues(
                    horizontal = dimens.padding.contentHorizontal,
                    vertical = dimens.padding.contentVertical,
                ),
            verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVertical),
            horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontal),
        ) {
            if (state != null) {
                content(
                    infoVisible = state.goalInfoVisible,
                    toggleInfoVisible = { viewModel.onAction(Action.SetGoalInfoVisible(false)) },
                    goalInput = state.input,
                )
            }
        }
    }
}

private fun LazyGridScope.content(
    infoVisible: Boolean,
    toggleInfoVisible: () -> Unit,
    goalInput: GoalInput,
) {
    if (infoVisible) {
        item(key = "info", span = { GridItemSpan(maxLineSpan) }) {
            Info(stringResource(id = R.string.goal_info), itemModifier) {
                InfoDefaults.DismissButton(toggleInfoVisible)
            }
        }
    }

    goalInput.minReps?.also { input ->
        item(key = "min_reps") {
            NumberInput(
                textFieldState = input.state,
                hint = stringResource(id = R.string.goal_min_reps),
                onPlusClick = { long -> input.state.updateValueBy(Increment.getReps(long)) },
                onMinusClick = { long -> input.state.updateValueBy(-Increment.getReps(long)) },
                keyboardOptions = getKeyboardOptions(goalInput.isLastInput(input)),
                keyboardActions = keyboardActions,
                modifier = itemModifier,
            )
        }
    }

    goalInput.maxReps?.also { input ->
        item(key = "max_reps") {
            NumberInput(
                textFieldState = input.state,
                hint = stringResource(id = R.string.goal_max_reps),
                onPlusClick = { long -> input.state.updateValueBy(Increment.getReps(long)) },
                onMinusClick = { long -> input.state.updateValueBy(-Increment.getReps(long)) },
                keyboardOptions = getKeyboardOptions(goalInput.isLastInput(input)),
                keyboardActions = keyboardActions,
                modifier = itemModifier,
            )
        }
    }

    goalInput.sets?.also { input ->
        item(key = "sets") {
            NumberInput(
                textFieldState = input.state,
                hint = stringResource(id = R.string.goal_sets),
                onPlusClick = { long -> input.state.updateValueBy(1) },
                onMinusClick = { long -> input.state.updateValueBy(-1) },
                keyboardOptions = getKeyboardOptions(goalInput.isLastInput(input)),
                keyboardActions = keyboardActions,
                modifier = itemModifier,
            )
        }
    }

    goalInput.duration?.also { input ->
        item(key = "duration") {
            InputFieldLayout(
                isError = input.state.hasError,
                label = { Text(text = stringResource(R.string.exercise_set_input_duration)) },
                modifier = itemModifier,
            ) {
                DurationPicker(
                    duration = input.state.value.milliseconds,
                    onDurationChange = { input.state.updateValue(it.inWholeMilliseconds) },
                    includeHours = true,
                )
            }
        }
    }

    goalInput.distance?.also { input ->
        item(key = "distance") {
            NumberInput(
                textFieldState = input.state,
                hint = stringResource(id = R.string.goal_distance),
                onPlusClick = { long -> input.state.updateValueBy(Increment.getDistance(long)) },
                onMinusClick = { long -> input.state.updateValueBy(-Increment.getDistance(long)) },
                suffix = input.unit.prettyString(),
                keyboardOptions = getKeyboardOptions(goalInput.isLastInput(input)),
                keyboardActions = keyboardActions,
                modifier = itemModifier,
            )
        }
    }

    goalInput.calories?.also { input ->
        item(key = "calories") {
            NumberInput(
                textFieldState = input.state,
                hint = stringResource(id = R.string.goal_calories),
                onPlusClick = { long -> input.state.updateValueBy(Increment.getCalories(long)) },
                onMinusClick = { long -> input.state.updateValueBy(-Increment.getCalories(long)) },
                keyboardOptions = getKeyboardOptions(goalInput.isLastInput(input)),
                keyboardActions = keyboardActions,
                modifier = itemModifier,
            )
        }
    }

    item(key = "divider", span = { GridItemSpan(maxLineSpan) }) {
        SinHorizontalDivider(
            horizontalExtent = LocalDimens.current.padding.contentHorizontal,
            modifier = itemModifier,
        )
    }

    item(key = "rest_time") {
        InputFieldLayout(
            isError = goalInput.restTime.state.hasError,
            label = { Text(text = stringResource(R.string.exercise_set_input_rest_time)) },
            modifier = itemModifier,
        ) {
            DurationPicker(
                duration = goalInput.restTime.state.value.milliseconds,
                onDurationChange = { goalInput.restTime.state.updateValue(it.inWholeMilliseconds) },
                includeHours = false,
            )
        }
    }
}

@MultiDevicePreview
@Composable
private fun ExerciseGoalPreview() {
    LiftAppTheme {
        val savedStateHandle = remember { SavedStateHandle() }
        val coroutineScope = rememberCoroutineScope { Dispatchers.Unconfined }
        val textFieldStateManager = PreviewResource.textFieldStateManager(savedStateHandle)
        val infoPreference = PreviewResource.preference(true)
        val unitPreference = PreviewResource.preference(LongDistanceUnit.Kilometer)
        val routeData = Routes.Exercise.goal(0, 0)
        val saveGoalContract: SaveGoalContract = interfaceStub()

        ExerciseGoalScreen(
            viewModel =
                remember {
                    ExerciseGoalViewModel(
                        getExerciseNameAndTypeUseCase =
                            GetExerciseNameAndTypeUseCase(
                                {
                                    flowOf(
                                        ExerciseNameAndType(
                                            Name.Raw("Bench Press"),
                                            ExerciseType.Weight,
                                        )
                                    )
                                },
                                routeData,
                            ),
                        getGoalUseCase =
                            GetGoalUseCase({ _, _ -> flowOf(Goal.default) }, routeData),
                        saveGoalUseCase = SaveGoalUseCase(saveGoalContract, routeData),
                        coroutineScope = coroutineScope,
                        textFieldStateManager = textFieldStateManager,
                        infoVisiblePreference = infoPreference,
                        longDistanceUnitPreference = unitPreference,
                        navigationCommander = NavigationCommander(),
                    )
                }
        )
    }
}

private val LazyGridItemScope.itemModifier: Modifier
    @Composable get() = Modifier.thenIf(!LocalInspectionMode.current) { Modifier.animateItem() }

private fun getKeyboardOptions(isLast: Boolean): KeyboardOptions =
    KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = if (isLast) ImeAction.Done else ImeAction.Next,
    )

val keyboardActions: KeyboardActions
    @Composable
    get() {
        val focusManager = LocalFocusManager.current
        return remember(focusManager) {
            KeyboardActions(onDone = { focusManager.clearFocus(true) })
        }
    }
