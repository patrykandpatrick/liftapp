package com.patrykandpatryk.liftapp.feature.newexercise.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.isLandscape
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.joinToPrettyString
import com.patrykandpatryk.liftapp.core.extension.thenIf
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.SupportingText
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
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

    Scaffold(
        modifier =
            modifier
                .thenIf(isLandscape) { navigationBarsPadding() }
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.title_new_exercise),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = { onAction(Action.PopBackStack) },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier =
                    Modifier.consumeWindowInsets(insets = WindowInsets.navigationBars).imePadding(),
                text = stringResource(id = R.string.action_save),
                icon = painterResource(id = R.drawable.ic_save),
                onClick = { onAction(Action.Save) },
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(
                        horizontal = MaterialTheme.dimens.padding.contentHorizontal,
                        vertical = MaterialTheme.dimens.padding.contentVertical,
                    ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.verticalItemSpacing),
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

    Column {
        OutlinedTextField(
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
            isError = state.showNameError,
        )

        SupportingText(
            visible = state.showNameError,
            text =
                stringResource(
                    id = R.string.error_x_empty,
                    stringResource(id = R.string.generic_name),
                ),
            isError = true,
        )
    }

    DropdownMenu(
        expanded = typeExpanded,
        onExpandedChange = setTypeExpanded,
        selectedItem = state.type,
        items = remember { ExerciseType.entries },
        getItemText = { it.prettyName },
        label = stringResource(id = R.string.generic_type),
        onClick = { onAction(Action.UpdateExerciseType(it)) },
    )

    DropdownMenu(
        expanded = mainMusclesExpanded,
        onExpandedChange = setMainMusclesExpanded,
        selectedItems = state.primaryMuscles.value,
        items = remember { Muscle.entries },
        getItemText = getMusclePrettyName,
        getItemsText = { it.joinToPrettyString(getMusclePrettyName) },
        label = stringResource(id = R.string.generic_main_muscles),
        onClick = { onAction(Action.ToggleMainMuscle(it)) },
        disabledItems = state.disabledMainMuscles,
        isError = state.showMainMusclesError,
        errorText = stringResource(id = R.string.error_pick_main_muscles),
    )

    DropdownMenu(
        expanded = secondaryMusclesExpanded,
        onExpandedChange = setSecondaryMusclesExpanded,
        selectedItems = state.secondaryMuscles,
        items = remember { Muscle.entries },
        getItemText = getMusclePrettyName,
        getItemsText = { it.joinToPrettyString(getMusclePrettyName) },
        label = stringResource(id = R.string.generic_secondary_muscles),
        onClick = { onAction(Action.ToggleSecondaryMuscle(it)) },
        disabledItems = state.disabledSecondaryMuscles,
    )

    DropdownMenu(
        expanded = tertiaryMusclesExpanded,
        onExpandedChange = setTertiaryMusclesExpanded,
        selectedItems = state.tertiaryMuscles,
        items = remember { Muscle.entries },
        getItemText = getMusclePrettyName,
        getItemsText = { it.joinToPrettyString(getMusclePrettyName) },
        label = stringResource(id = R.string.generic_tertiary_muscles),
        onClick = { onAction(Action.ToggleTertiaryMuscle(it)) },
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
