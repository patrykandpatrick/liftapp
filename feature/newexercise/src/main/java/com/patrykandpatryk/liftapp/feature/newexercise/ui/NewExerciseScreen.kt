package com.patrykandpatryk.liftapp.feature.newexercise.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.isLandscape
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.joinToPrettyString
import com.patrykandpatryk.liftapp.core.extension.thenIf
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatryk.liftapp.core.ui.resource.getMusclePrettyName
import com.patrykandpatryk.liftapp.core.ui.resource.prettyName
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.feature.newexercise.model.Action
import com.patrykandpatryk.liftapp.feature.newexercise.model.NewExerciseState

@Composable
fun NewExerciseScreen(modifier: Modifier = Modifier) {
    val viewModel: NewExerciseViewModel = hiltViewModel()

    val snackbarHostState = remember { SnackbarHostState() }
    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    NewExerciseScreen(
        state = viewModel.state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun NewExerciseScreen(
    state: NewExerciseState,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LiftAppScaffold(
        modifier =
            modifier
                .thenIf(isLandscape) { navigationBarsPadding() }
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            CompactTopAppBar(
                title = { Text(stringResource(id = R.string.title_new_exercise)) },
                navigationIcon = {
                    CompactTopAppBarDefaults.BackIcon(onClick = { onAction(Action.PopBackStack) })
                },
            )
        },
        bottomBar = { BottomAppBar.Save(onClick = { onAction(Action.Save) }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(
                        horizontal = dimens.padding.contentHorizontal,
                        vertical = dimens.padding.contentVertical,
                    ),
            verticalArrangement = Arrangement.spacedBy(dimens.verticalItemSpacing),
        ) {
            Content(state = state, onAction = onAction)
        }
    }
}

@Composable
private fun Content(state: NewExerciseState, onAction: (Action) -> Unit) {
    val (typeExpanded, setTypeExpanded) = remember { mutableStateOf(false) }
    val (mainMusclesExpanded, setMainMusclesExpanded) = remember { mutableStateOf(false) }
    val (secondaryMusclesExpanded, setSecondaryMusclesExpanded) = remember { mutableStateOf(false) }
    val (tertiaryMusclesExpanded, setTertiaryMusclesExpanded) = remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LiftAppTextFieldWithSupportingText(
        modifier = Modifier.fillMaxWidth(),
        value = state.displayName,
        onValueChange = { onAction(Action.UpdateName(it)) },
        label = { Text(text = stringResource(id = R.string.generic_name)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions =
            KeyboardActions {
                keyboardController?.hide()
                focusManager.clearFocus(force = true)
            },
        errorText =
            if (state.showNameError) {
                AnnotatedString(
                    stringResource(
                        id = R.string.error_x_empty,
                        stringResource(id = R.string.generic_name),
                    )
                )
            } else {
                null
            },
    )

    ListTextField(
        expanded = typeExpanded,
        onExpandedChange = setTypeExpanded,
        selectedItem = state.type,
        items = remember { ExerciseType.entries },
        getItemText = { it.prettyName },
        label = stringResource(id = R.string.generic_type),
        onClick = { onAction(Action.UpdateExerciseType(it)) },
    )

    ListTextField(
        expanded = mainMusclesExpanded,
        onExpandedChange = setMainMusclesExpanded,
        selectedItems = state.primaryMuscles.value,
        items = remember { Muscle.entries },
        getItemText = getMusclePrettyName,
        getItemsText = { it.joinToPrettyString(getMusclePrettyName) },
        label = stringResource(id = R.string.generic_main_muscles),
        onClick = { onAction(Action.MainMuscleListAction(Action.ListAction.ToggleMuscle(it))) },
        onClear = { onAction(Action.MainMuscleListAction(Action.ListAction.Clear)) },
        disabledItems = state.disabledMainMuscles,
        isError = state.showMainMusclesError,
        errorText = stringResource(id = R.string.error_pick_main_muscles),
    )

    ListTextField(
        expanded = secondaryMusclesExpanded,
        onExpandedChange = setSecondaryMusclesExpanded,
        selectedItems = state.secondaryMuscles,
        items = remember { Muscle.entries },
        getItemText = getMusclePrettyName,
        getItemsText = { it.joinToPrettyString(getMusclePrettyName) },
        label = stringResource(id = R.string.generic_secondary_muscles),
        onClick = {
            onAction(Action.SecondaryMuscleListAction(Action.ListAction.ToggleMuscle(it)))
        },
        onClear = { onAction(Action.SecondaryMuscleListAction(Action.ListAction.Clear)) },
        disabledItems = state.disabledSecondaryMuscles,
    )

    ListTextField(
        expanded = tertiaryMusclesExpanded,
        onExpandedChange = setTertiaryMusclesExpanded,
        selectedItems = state.tertiaryMuscles,
        items = remember { Muscle.entries },
        getItemText = getMusclePrettyName,
        getItemsText = { it.joinToPrettyString(getMusclePrettyName) },
        label = stringResource(id = R.string.generic_tertiary_muscles),
        onClick = { onAction(Action.TertiaryMuscleListAction(Action.ListAction.ToggleMuscle(it))) },
        onClear = { onAction(Action.TertiaryMuscleListAction(Action.ListAction.Clear)) },
        disabledItems = state.disabledTertiaryMuscles,
    )
}

@MultiDevicePreview
@Composable
fun PreviewNewExerciseLight() {
    PreviewNewExercise()
}

@Composable
private fun PreviewNewExercise() {
    LiftAppTheme {
        NewExerciseScreen(
            state = NewExerciseState.Invalid(),
            onAction = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
