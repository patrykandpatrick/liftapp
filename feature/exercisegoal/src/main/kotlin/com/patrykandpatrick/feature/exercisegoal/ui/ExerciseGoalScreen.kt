package com.patrykandpatrick.feature.exercisegoal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.feature.exercisegoal.model.GetExerciseNameAndTypeUseCase
import com.patrykandpatrick.feature.exercisegoal.model.GetGoalUseCase
import com.patrykandpatrick.feature.exercisegoal.model.GoalInput
import com.patrykandpatrick.feature.exercisegoal.model.SaveGoalUseCase
import com.patrykandpatrick.feature.exercisegoal.navigation.ExerciseGoalNavigator
import com.patrykandpatrick.feature.exercisegoal.navigation.ExerciseGoalRouteData
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.interfaceStub
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
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.input.NumberInput
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.wheel.DurationPicker
import com.patrykandpatryk.liftapp.domain.Constants.Input.Increment
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseNameAndType
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.goal.SaveGoalContract
import com.patrykandpatryk.liftapp.domain.model.Name
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf

@Composable
fun ExerciseGoalScreen(
    navigator: ExerciseGoalNavigator,
    modifier: Modifier = Modifier,
    viewModel: ExerciseGoalViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val dimens = LocalDimens.current

    LaunchedEffect(state) { if (state?.goalSaved == true) navigator.back() }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = state?.exerciseName?.getDisplayName().orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = navigator::back) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_close),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.setGoalInfoVisible(state?.goalInfoVisible != true) }
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
                BottomAppBar.Save(onClick = { viewModel.save(state) })
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
                    toggleInfoVisible = { viewModel.setGoalInfoVisible(false) },
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
            Info(
                stringResource(id = R.string.goal_info),
                Modifier.thenIf(!LocalInspectionMode.current) { Modifier.animateItem() },
            ) {
                InfoDefaults.DismissButton(toggleInfoVisible)
            }
        }
    }

    if (goalInput.minReps != null) {
        item(key = "min_reps") {
            NumberInput(
                textFieldState = goalInput.minReps.state,
                hint = stringResource(id = R.string.goal_min_reps),
                onPlusClick = { long ->
                    goalInput.minReps.state.updateValueBy(Increment.getReps(long))
                },
                onMinusClick = { long ->
                    goalInput.minReps.state.updateValueBy(-Increment.getReps(long))
                },
                keyboardOptions = keyboardOptions,
                modifier = Modifier.thenIf(!LocalInspectionMode.current) { Modifier.animateItem() },
            )
        }
    }

    if (goalInput.maxReps != null) {
        item(key = "max_reps") {
            NumberInput(
                textFieldState = goalInput.maxReps.state,
                hint = stringResource(id = R.string.goal_max_reps),
                onPlusClick = { long ->
                    goalInput.maxReps.state.updateValueBy(Increment.getReps(long))
                },
                onMinusClick = { long ->
                    goalInput.maxReps.state.updateValueBy(-Increment.getReps(long))
                },
                keyboardOptions = keyboardOptions,
                modifier = Modifier.thenIf(!LocalInspectionMode.current) { Modifier.animateItem() },
            )
        }
    }

    if (goalInput.sets != null) {
        item(key = "sets") {
            NumberInput(
                textFieldState = goalInput.sets.state,
                hint = stringResource(id = R.string.goal_sets),
                onPlusClick = { long -> goalInput.sets.state.updateValueBy(1) },
                onMinusClick = { long -> goalInput.sets.state.updateValueBy(-1) },
                keyboardOptions = keyboardOptions,
                modifier = Modifier.thenIf(!LocalInspectionMode.current) { Modifier.animateItem() },
            )
        }
    }

    item(key = "rest_time") {
        InputFieldLayout(
            isError = goalInput.restTime.state.hasError,
            label = { Text(text = stringResource(R.string.exercise_set_input_duration)) },
            modifier = Modifier.thenIf(!LocalInspectionMode.current) { Modifier.animateItem() },
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
        val preference = PreviewResource.preference(true)
        val routeData = ExerciseGoalRouteData(0, 0)
        val saveGoalContract: SaveGoalContract = interfaceStub()

        ExerciseGoalScreen(
            navigator = interfaceStub(),
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
                            GetGoalUseCase({ _, _ -> flowOf(Goal.Default) }, routeData),
                        saveGoalUseCase = SaveGoalUseCase(saveGoalContract, routeData),
                        coroutineScope = coroutineScope,
                        textFieldStateManager = textFieldStateManager,
                        infoVisiblePreference = preference,
                    )
                },
        )
    }
}

private val keyboardOptions: KeyboardOptions
    get() = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
