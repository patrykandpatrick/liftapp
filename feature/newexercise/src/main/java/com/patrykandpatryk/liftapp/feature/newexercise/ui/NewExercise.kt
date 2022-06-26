package com.patrykandpatryk.liftapp.feature.newexercise.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.ProvideLandscapeMode
import com.patrykandpatryk.liftapp.core.extension.isLandscape
import com.patrykandpatryk.liftapp.core.extension.joinToPrettyString
import com.patrykandpatryk.liftapp.core.extension.thenIf
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.resource.getMusclePrettyName
import com.patrykandpatryk.liftapp.core.ui.resource.prettyName
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.topAppBarScrollBehavior
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.feature.newexercise.state.ScreenState

@Composable
fun NewExercise(
    modifier: Modifier = Modifier,
    popBackStack: () -> Unit,
) {
    val viewModel: NewExerciseViewModel = hiltViewModel()

    NewExercise(
        modifier = modifier,
        state = viewModel.state,
        updateName = viewModel::updateName,
        updateExerciseType = viewModel::updateExerciseType,
        updateMainMuscles = viewModel::updateMainMuscles,
        updateSecondaryMuscles = viewModel::updateSecondaryMuscles,
        updateTertiaryMuscles = viewModel::updateTertiaryMuscles,
        popBackStack = popBackStack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewExercise(
    modifier: Modifier = Modifier,
    state: ScreenState,
    updateName: (String) -> Unit,
    updateExerciseType: (ExerciseType) -> Unit,
    updateMainMuscles: (Muscle) -> Unit,
    updateSecondaryMuscles: (Muscle) -> Unit,
    updateTertiaryMuscles: (Muscle) -> Unit,
    popBackStack: () -> Unit,
) {
    val topAppBarScrollBehavior = topAppBarScrollBehavior()

    Scaffold(
        modifier = modifier
            .thenIf(isLandscape) { navigationBarsPadding() }
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.title_new_exercise),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = popBackStack,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.navigationBarsPadding(),
                text = stringResource(id = R.string.action_save),
                icon = painterResource(id = R.drawable.ic_save),
                onClick = { popBackStack() },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(
                    horizontal = MaterialTheme.dimens.padding.contentHorizontal,
                    vertical = MaterialTheme.dimens.padding.contentVertical,
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.verticalItemSpacing),
        ) {

            Content(
                state = state,
                updateName = updateName,
                updateExerciseType = updateExerciseType,
                updateMainMuscles = updateMainMuscles,
                updateSecondaryMuscles = updateSecondaryMuscles,
                updateTertiaryMuscles = updateTertiaryMuscles,
            )
        }
    }
}

@Composable
private fun ColumnScope.Content(
    state: ScreenState,
    updateName: (String) -> Unit,
    updateExerciseType: (ExerciseType) -> Unit,
    updateMainMuscles: (Muscle) -> Unit,
    updateSecondaryMuscles: (Muscle) -> Unit,
    updateTertiaryMuscles: (Muscle) -> Unit,
) {

    val (typeExpanded, setTypeExpanded) = remember { mutableStateOf(false) }
    val (mainMusclesExpanded, setMainMusclesExpanded) = remember { mutableStateOf(false) }
    val (secondaryMusclesExpanded, setSecondaryMusclesExpanded) = remember { mutableStateOf(false) }
    val (tertiaryMusclesExpanded, setTertiaryMusclesExpanded) = remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.name,
        onValueChange = updateName,
        label = { Text(text = stringResource(id = R.string.generic_name)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    )

    DropdownMenu(
        expanded = typeExpanded,
        onExpandedChange = setTypeExpanded,
        selectedItem = state.type,
        items = remember { ExerciseType.values().toList() },
        getItemText = { it.prettyName },
        label = stringResource(id = R.string.generic_type),
        onClick = updateExerciseType,
    )

    DropdownMenu(
        expanded = mainMusclesExpanded,
        onExpandedChange = setMainMusclesExpanded,
        selectedItems = state.mainMuscles,
        items = remember { Muscle.values().toList() },
        getItemText = getMusclePrettyName,
        getItemsText = { it.joinToPrettyString(getMusclePrettyName) },
        label = stringResource(id = R.string.generic_main_muscles),
        onClick = updateMainMuscles,
        disabledItems = state.disabledMainMuscles,
    )

    DropdownMenu(
        expanded = secondaryMusclesExpanded,
        onExpandedChange = setSecondaryMusclesExpanded,
        selectedItems = state.secondaryMuscles,
        items = remember { Muscle.values().toList() },
        getItemText = getMusclePrettyName,
        getItemsText = { it.joinToPrettyString(getMusclePrettyName) },
        label = stringResource(id = R.string.generic_secondary_muscles),
        onClick = updateSecondaryMuscles,
        disabledItems = state.disabledSecondaryMuscles,
    )

    DropdownMenu(
        expanded = tertiaryMusclesExpanded,
        onExpandedChange = setTertiaryMusclesExpanded,
        selectedItems = state.tertiaryMuscles,
        items = remember { Muscle.values().toList() },
        getItemText = getMusclePrettyName,
        getItemsText = { it.joinToPrettyString(getMusclePrettyName) },
        label = stringResource(id = R.string.generic_tertiary_muscles),
        onClick = updateTertiaryMuscles,
        disabledItems = state.disabledTertiaryMuscles,
    )
}

@Preview()
@Composable
fun PreviewNewExerciseLight() {
    PreviewNewExercise(darkTheme = false)
}

@Preview(widthDp = 720, heightDp = 360)
@Composable
fun PreviewNewExerciseLightLandscape() {
    ProvideLandscapeMode {
        PreviewNewExercise(darkTheme = false)
    }
}

@Preview
@Composable
fun PreviewNewExerciseDark() {
    PreviewNewExercise(darkTheme = true)
}

@Preview(widthDp = 720, heightDp = 360)
@Composable
fun PreviewNewExerciseDarkLandscape() {
    ProvideLandscapeMode {
        PreviewNewExercise(darkTheme = true)
    }
}

@Composable
private fun PreviewNewExercise(darkTheme: Boolean) {
    LiftAppTheme(darkTheme = darkTheme) {
        NewExercise(
            state = ScreenState(),
            updateName = {},
            updateExerciseType = {},
            updateMainMuscles = {},
            updateSecondaryMuscles = {},
            updateTertiaryMuscles = {},
            popBackStack = {},
        )
    }
}
