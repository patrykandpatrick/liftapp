package com.patrykandpatrick.liftapp.feature.newexercise.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.extension.thenIf
import com.patrykandpatrick.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.ui.BottomAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatrick.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatrick.liftapp.core.ui.animation.ENTER_ANIM_DURATION
import com.patrykandpatrick.liftapp.core.ui.animation.EXIT_ANIM_DURATION
import com.patrykandpatrick.liftapp.core.ui.resource.getExerciseRolePrettyName
import com.patrykandpatrick.liftapp.core.ui.resource.getMusclePrettyName
import com.patrykandpatrick.liftapp.core.ui.resource.prettyList
import com.patrykandpatrick.liftapp.core.ui.resource.prettyName
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.exercise.ProgressionPolicy
import com.patrykandpatrick.liftapp.domain.exercise.carriesLoad
import com.patrykandpatrick.liftapp.domain.exercise.hasRole
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.feature.newexercise.model.Action
import com.patrykandpatrick.liftapp.feature.newexercise.model.NewExerciseState
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.isLandscape
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

@Composable
fun NewExerciseScreen(modifier: Modifier = Modifier) {
    val viewModel: NewExerciseViewModel = hiltViewModel()

    val snackbarHostState = remember { SnackbarHostState() }
    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    NewExerciseScreen(
        state = viewModel.state,
        isEdit = viewModel.isEdit,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun NewExerciseScreen(
    state: NewExerciseState,
    isEdit: Boolean,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LiftAppScaffold(
        modifier =
            modifier
                .thenIf(isLandscape) { navigationBarsPadding() }
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            CompactTopAppBar(
                scrollBehavior = topAppBarScrollBehavior,
                title = {
                    Text(
                        stringResource(
                            if (isEdit) {
                                R.string.title_edit_exercise
                            } else {
                                R.string.title_new_exercise
                            }
                        )
                    )
                },
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
                        start = dimens.screen.padding,
                        top = dimens.screen.padding,
                        end = dimens.screen.padding,
                    )
        ) {
            Content(state = state, onAction = onAction)
        }
    }
}

/**
 * The gap below a field, carried by the field rather than by the [Column] that holds it, so that a
 * field which animates away takes its share of the spacing with it. The trailing gap under the last
 * field doubles as the form's bottom padding.
 */
private val FieldSpacing = 16.dp

private val fieldModifier = Modifier.fillMaxWidth().padding(bottom = FieldSpacing)

@Composable
private fun ColumnScope.Content(state: NewExerciseState, onAction: (Action) -> Unit) {
    val (typeExpanded, setTypeExpanded) = remember { mutableStateOf(false) }
    val (roleExpanded, setRoleExpanded) = remember { mutableStateOf(false) }
    val (progressionExpanded, setProgressionExpanded) = remember { mutableStateOf(false) }
    val (mainMusclesExpanded, setMainMusclesExpanded) = remember { mutableStateOf(false) }
    val (secondaryMusclesExpanded, setSecondaryMusclesExpanded) = remember { mutableStateOf(false) }
    val (tertiaryMusclesExpanded, setTertiaryMusclesExpanded) = remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LiftAppTextFieldWithSupportingText(
        modifier = fieldModifier,
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
        modifier = fieldModifier,
        expanded = typeExpanded,
        onExpandedChange = setTypeExpanded,
        selectedItem = state.type,
        items = remember { ExerciseType.entries },
        getItemText = { it.prettyName },
        label = stringResource(id = R.string.generic_type),
        onClick = { onAction(Action.UpdateExerciseType(it)) },
    )

    // Both fields are shown only where the type admits more than one answer. Cardio and a timed
    // hold have no role at all, and nothing that records no load can progress one, so offering the
    // choice there would be a question with a single correct answer.
    HideableField(visible = state.type.hasRole) {
        // Null is a value in this list, not an absence — it is how the user says an exercise is
        // one they would rather not classify.
        ListTextField(
            modifier = fieldModifier,
            expanded = roleExpanded,
            onExpandedChange = setRoleExpanded,
            selectedItem = state.role,
            items = remember { listOf(null) + ExerciseRole.entries },
            getItemText = getExerciseRolePrettyName,
            label = stringResource(id = R.string.generic_role),
            onClick = { onAction(Action.UpdateRole(it)) },
        )
    }

    HideableField(visible = state.type.carriesLoad) {
        ListTextField(
            modifier = fieldModifier,
            expanded = progressionExpanded,
            onExpandedChange = setProgressionExpanded,
            selectedItem = state.progressionPolicy,
            items = remember { ProgressionPolicy.entries },
            getItemText = { it.prettyName },
            label = stringResource(id = R.string.generic_progression),
            onClick = { onAction(Action.UpdateProgressionPolicy(it)) },
        )
    }

    ListTextField(
        modifier = fieldModifier,
        expanded = mainMusclesExpanded,
        onExpandedChange = setMainMusclesExpanded,
        selectedItems = state.primaryMuscles.value,
        items = remember { Muscle.entries },
        getItemText = getMusclePrettyName,
        getItemsText = { it.prettyList() },
        label = stringResource(id = R.string.generic_main_muscles),
        onClick = { onAction(Action.MainMuscleListAction(Action.ListAction.ToggleMuscle(it))) },
        onClear = { onAction(Action.MainMuscleListAction(Action.ListAction.Clear)) },
        disabledItems = state.disabledMainMuscles,
        isError = state.showMainMusclesError,
        errorText = stringResource(id = R.string.error_pick_main_muscles),
    )

    ListTextField(
        modifier = fieldModifier,
        expanded = secondaryMusclesExpanded,
        onExpandedChange = setSecondaryMusclesExpanded,
        selectedItems = state.secondaryMuscles,
        items = remember { Muscle.entries },
        getItemText = getMusclePrettyName,
        getItemsText = { it.prettyList() },
        label = stringResource(id = R.string.generic_secondary_muscles),
        onClick = {
            onAction(Action.SecondaryMuscleListAction(Action.ListAction.ToggleMuscle(it)))
        },
        onClear = { onAction(Action.SecondaryMuscleListAction(Action.ListAction.Clear)) },
        disabledItems = state.disabledSecondaryMuscles,
    )

    ListTextField(
        modifier = fieldModifier,
        expanded = tertiaryMusclesExpanded,
        onExpandedChange = setTertiaryMusclesExpanded,
        selectedItems = state.tertiaryMuscles,
        items = remember { Muscle.entries },
        getItemText = getMusclePrettyName,
        getItemsText = { it.prettyList() },
        label = stringResource(id = R.string.generic_tertiary_muscles),
        onClick = { onAction(Action.TertiaryMuscleListAction(Action.ListAction.ToggleMuscle(it))) },
        onClear = { onAction(Action.TertiaryMuscleListAction(Action.ListAction.Clear)) },
        disabledItems = state.disabledTertiaryMuscles,
    )
}

/**
 * A field only some exercise types have, which grows and fades in rather than appearing between two
 * others without warning.
 *
 * The gap below the field belongs to the field itself — see [fieldModifier] — so it collapses along
 * with it. Were the spacing the [Column]'s instead, a child of zero height would still have spacing
 * reserved on both sides of it, and everything below would drop by one gap in a single frame at the
 * very end of an otherwise smooth animation.
 */
@Composable
private fun ColumnScope.HideableField(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter =
            expandVertically(animationSpec = tween(ENTER_ANIM_DURATION)) +
                fadeIn(animationSpec = tween(ENTER_ANIM_DURATION)),
        exit =
            shrinkVertically(animationSpec = tween(EXIT_ANIM_DURATION)) +
                fadeOut(animationSpec = tween(EXIT_ANIM_DURATION)),
    ) {
        content()
    }
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
            isEdit = false,
            onAction = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
