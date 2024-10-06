package com.patrykandpatrick.feature.exercisegoal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.feature.exercisegoal.navigation.ExerciseGoalNavigator
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.interfaceStub
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.Info
import com.patrykandpatryk.liftapp.core.ui.OutlinedTextField
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.wheel.WheelPickerDurationInputField
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.model.Name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf

@Composable
fun ExerciseGoalScreen(
    navigator: ExerciseGoalNavigator,
    routineID: Long,
    exerciseID: Long,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<ExerciseGoalViewModel, ExerciseGoalViewModel.Factory>(
        creationCallback = { factory -> factory.create(routineID, exerciseID) }
    )

    val state = viewModel.state

    ExerciseGoalScreen(
        navigator = navigator,
        state = state,
        modifier = modifier,
    )

    LaunchedEffect(state) {
        state.goalSaved.collect { if (it) navigator.back() }
    }
}

@Composable
private fun ExerciseGoalScreen(
    navigator: ExerciseGoalNavigator,
    state: ExerciseGoalState,
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current
    val infoVisible = remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier,
        topBar = {
            val exerciseName = state.exerciseName.collectAsStateWithLifecycle().value
            CenterAlignedTopAppBar(
                title = { Text(text = exerciseName) },
                navigationIcon = {
                    IconButton(onClick = navigator::back) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_close),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { infoVisible.value = !infoVisible.value }) {
                        Icon(
                            imageVector = Icons.TwoTone.Info,
                            contentDescription = stringResource(id = R.string.action_info),
                        )
                    }
                }
            )
        },
        bottomBar = { BottomAppBar.Save(onClick = state::save) },
    ) { paddingValues ->
        LazyVerticalGrid(
            modifier = modifier
                .imePadding()
                .padding(paddingValues)
                .fillMaxSize(),
            columns = GridCells.Adaptive(minSize = dimens.grid.minCellWidthLarge),
            contentPadding = PaddingValues(
                horizontal = dimens.padding.contentHorizontal,
                vertical = dimens.padding.contentVertical,
            ),
            verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVertical),
            horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontal),
        ) {
            if (infoVisible.value) {
                item(
                    key = "info",
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    Info(
                        stringResource(id = R.string.goal_info),
                        modifier = Modifier.animateItem(),
                    )
                }
            }

            item(key = "min_reps") {
                OutlinedTextField(
                    textFieldState = state.minReps,
                    label = { Text(stringResource(id = R.string.goal_min_reps)) },
                    singleLine = true,
                    keyboardOptions = keyboardOptions,
                    modifier = Modifier.animateItem(),
                )
            }

            item(key = "max_reps") {
                OutlinedTextField(
                    textFieldState = state.maxReps,
                    label = { Text(stringResource(id = R.string.goal_max_reps)) },
                    singleLine = true,
                    keyboardOptions = keyboardOptions,
                    modifier = Modifier.animateItem(),
                )
            }

            item(key = "sets") {
                OutlinedTextField(
                    textFieldState = state.sets,
                    label = { Text(stringResource(id = R.string.goal_sets)) },
                    singleLine = true,
                    keyboardOptions = keyboardOptions,
                    modifier = Modifier.animateItem(),
                )
            }

            item(key = "rest_time") {
                WheelPickerDurationInputField(
                    text = state.formattedRestTime.collectAsStateWithLifecycle().value,
                    duration = state.restTime.collectAsStateWithLifecycle().value,
                    onTimeChange = state::setRestTime,
                    label = stringResource(id = R.string.goal_rest_time),
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun ExerciseGoalPreview() {
    LiftAppTheme {
        val savedStateHandle = SavedStateHandle()
        val formatter = PreviewResource.formatter()

        ExerciseGoalScreen(
            navigator = interfaceStub(),
            state = ExerciseGoalState(
                exercise = flowOf(
                    Exercise(
                        id = 1L,
                        displayName = "Bench Press",
                        name = Name.Raw("Bench Press"),
                        exerciseType = ExerciseType.Weight,
                        mainMuscles = emptyList(),
                        secondaryMuscles = emptyList(),
                        tertiaryMuscles = emptyList(),
                    )
                ),
                goal = flowOf(Goal.Default),
                saveGoal = {},
                getFormattedDuration = { formatter.getFormattedDuration(it, Formatter.DateFormat.MinutesSeconds) },
                coroutineScope = rememberCoroutineScope { Dispatchers.Unconfined },
                savedStateHandle = savedStateHandle,
                textFieldStateManager = PreviewResource.textFieldStateManager(savedStateHandle),
            )
        )
    }
}

private val keyboardOptions: KeyboardOptions
    get() = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next,
    )
