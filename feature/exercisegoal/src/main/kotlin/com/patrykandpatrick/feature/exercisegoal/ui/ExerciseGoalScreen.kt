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
import com.patrykandpatrick.feature.exercisegoal.model.GetExerciseNameUseCase
import com.patrykandpatrick.feature.exercisegoal.model.GetGoalUseCase
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
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.Info
import com.patrykandpatryk.liftapp.core.ui.InfoDefaults
import com.patrykandpatryk.liftapp.core.ui.OutlinedTextField
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.wheel.WheelPickerDurationInputField
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.goal.SaveGoalContract
import com.patrykandpatryk.liftapp.domain.model.Name
import kotlin.time.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ExerciseGoalScreen(
    navigator: ExerciseGoalNavigator,
    modifier: Modifier = Modifier,
    viewModel: ExerciseGoalViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) { viewModel.goalSaved.collect { if (it) navigator.back() } }

    val dimens = LocalDimens.current
    val infoVisible = viewModel.goalInfoVisible.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            val exerciseName =
                viewModel.exerciseName.collectAsStateWithLifecycle().value.getDisplayName()
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
                    IconButton(onClick = viewModel::toggleGoalInfoVisible) {
                        Icon(
                            imageVector = Icons.TwoTone.Info,
                            contentDescription = stringResource(id = R.string.action_info),
                        )
                    }
                },
            )
        },
        bottomBar = { BottomAppBar.Save(onClick = viewModel::save) },
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
            infoVisible.value?.also { infoVisible ->
                content(
                    infoVisible = infoVisible,
                    toggleInfoVisible = viewModel::toggleGoalInfoVisible,
                    minReps = viewModel.minReps,
                    maxReps = viewModel.maxReps,
                    sets = viewModel.sets,
                    formattedRestTime = viewModel.formattedRestTime,
                    restTime = viewModel.restTime,
                    setRestTime = viewModel::setRestTime,
                )
            }
        }
    }
}

private fun LazyGridScope.content(
    infoVisible: Boolean,
    toggleInfoVisible: () -> Unit,
    minReps: TextFieldState<Int>,
    maxReps: TextFieldState<Int>,
    sets: TextFieldState<Int>,
    formattedRestTime: StateFlow<String>,
    restTime: StateFlow<Duration>,
    setRestTime: (Duration) -> Unit,
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

    item(key = "min_reps") {
        OutlinedTextField(
            textFieldState = minReps,
            label = { Text(stringResource(id = R.string.goal_min_reps)) },
            singleLine = true,
            keyboardOptions = keyboardOptions,
            modifier = Modifier.thenIf(!LocalInspectionMode.current) { Modifier.animateItem() },
        )
    }

    item(key = "max_reps") {
        OutlinedTextField(
            textFieldState = maxReps,
            label = { Text(stringResource(id = R.string.goal_max_reps)) },
            singleLine = true,
            keyboardOptions = keyboardOptions,
            modifier = Modifier.thenIf(!LocalInspectionMode.current) { Modifier.animateItem() },
        )
    }

    item(key = "sets") {
        OutlinedTextField(
            textFieldState = sets,
            label = { Text(stringResource(id = R.string.goal_sets)) },
            singleLine = true,
            keyboardOptions = keyboardOptions,
            modifier = Modifier.thenIf(!LocalInspectionMode.current) { Modifier.animateItem() },
        )
    }

    item(key = "rest_time") {
        WheelPickerDurationInputField(
            text = formattedRestTime.collectAsStateWithLifecycle().value,
            duration = restTime.collectAsStateWithLifecycle().value,
            onTimeChange = setRestTime,
            label = stringResource(id = R.string.goal_rest_time),
            modifier = Modifier.thenIf(!LocalInspectionMode.current) { Modifier.animateItem() },
        )
    }
}

@MultiDevicePreview
@Composable
private fun ExerciseGoalPreview() {
    LiftAppTheme {
        val savedStateHandle = remember { SavedStateHandle() }
        val formatter = PreviewResource.formatter()
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
                        getExerciseNameUseCase =
                            GetExerciseNameUseCase({ flowOf(Name.Raw("Bench Press")) }, routeData),
                        getGoalUseCase =
                            GetGoalUseCase({ _, _ -> flowOf(Goal.Default) }, routeData),
                        saveGoalUseCase = SaveGoalUseCase(saveGoalContract, routeData),
                        formatter = formatter,
                        coroutineScope = coroutineScope,
                        savedStateHandle = savedStateHandle,
                        textFieldStateManager = textFieldStateManager,
                        infoVisiblePreference = preference,
                    )
                },
        )
    }
}

private val keyboardOptions: KeyboardOptions
    get() = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
