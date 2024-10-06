package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.interfaceStub
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.OutlinedTextField
import com.patrykandpatryk.liftapp.core.ui.animateJump
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.resource.iconRes
import com.patrykandpatryk.liftapp.core.ui.theme.Colors.IllustrationAlpha
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.validation.NonEmptyCollectionValidator
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatryk.liftapp.feature.newroutine.navigation.NewRoutineNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf

@Composable
fun NewRoutineScreen(
    routineID: Long,
    navigator: NewRoutineNavigator,
    modifier: Modifier = Modifier,
) {
    val viewModel: NewRoutineViewModel = hiltViewModel(
        creationCallback = { factory: NewRoutineViewModel.Factory -> factory.create(routineID) },
    )

    NewRoutineScreen(
        state = viewModel.state,
        navigator = navigator,
        modifier = modifier,
    )
}

@Composable
private fun NewRoutineScreen(
    state: NewRoutineState,
    navigator: NewRoutineNavigator,
    modifier: Modifier = Modifier,
) {
    val routineSaved by state.routineSaved

    val routineNotFound by state.routineNotFound

    LaunchedEffect(Unit) {
        navigator.registerResultListener(key = Constants.Keys.PICKED_EXERCISE_IDS) { pickedExerciseIds: List<Long> ->
            state.addPickedExercises(pickedExerciseIds)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(if (state.isEdit) R.string.title_edit_routine else R.string.title_new_routine))
                },
                navigationIcon = {
                    IconButton(onClick = navigator::back) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_close),
                        )
                    }
                },
            )
        },
        bottomBar = { BottomAppBar.Save(onClick = state::save) },
    ) { paddingValues ->
        if (currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
            NewRoutineCompactContent(
                state = state,
                pickExercises = { navigator.pickExercises(state.exerciseIds) },
                modifier = Modifier.padding(paddingValues),
            )
        } else {
            NewRoutineContentLarge(
                state = state,
                pickExercises = { navigator.pickExercises(state.exerciseIds) },
                modifier = Modifier.padding(paddingValues),
            )
        }
    }

    LaunchedEffect(routineNotFound, routineSaved) {
        if (routineNotFound || routineSaved) navigator.back()
    }
}

@Composable
private fun NewRoutineCompactContent(
    state: NewRoutineState,
    pickExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LocalDimens.current.verticalItemSpacing),
        modifier = modifier
            .padding(
                horizontal = LocalDimens.current.padding.contentHorizontal,
                vertical = LocalDimens.current.padding.contentVertical,
            )
    ) {
        OutlinedTextField(
            textFieldState = state.name,
            label = { Text(text = stringResource(id = R.string.generic_name)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { state.save() }),
            maxLines = LocalDimens.current.input.nameMaxLines,
            modifier = Modifier.fillMaxWidth(),
        )

        Exercises(
            state = state,
            pickExercises = pickExercises,
            removeExercises = {},
        )
    }
}

@Composable
private fun NewRoutineContentLarge(
    state: NewRoutineState,
    pickExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = LocalDimens.current.padding.itemHorizontal),
        modifier = modifier
            .padding(
                horizontal = LocalDimens.current.padding.contentHorizontal,
                vertical = LocalDimens.current.padding.contentVertical,
            )
    ) {
        OutlinedTextField(
            textFieldState = state.name,
            label = { Text(text = stringResource(id = R.string.generic_name)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { state.save() }),
            maxLines = LocalDimens.current.input.nameMaxLines,
            modifier = Modifier.weight(1f),
        )

        Exercises(
            state = state,
            pickExercises = pickExercises,
            removeExercises = {},
            modifier = Modifier
                .weight(1f)
                .padding(top = 6.dp),
        )
    }
}

@Composable
private fun Exercises(
    state: NewRoutineState,
    pickExercises: () -> Unit,
    removeExercises: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val exercises = state.exercises.collectAsStateWithLifecycle()
    OutlinedCard(
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = LocalDimens.current.verticalItemSpacing),
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = LocalDimens.current.verticalItemSpacing,
            ),
        ) {
            ListSectionTitle(
                title = stringResource(R.string.title_exercises),
                paddingValues = PaddingValues(),
                trailingIcon = {
                    IconButton(onClick = removeExercises) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = null,
                        )
                    }
                },
                modifier = Modifier
                    .padding(start = LocalDimens.current.padding.itemHorizontal, end = 8.dp),
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                if (exercises.value.isInvalid) {
                    item {
                        EmptyState(state, Modifier.fillParentMaxSize())
                    }
                } else {
                    items(
                        items = exercises.value.value,
                        key = { it.id },
                        contentType = { it::class },
                    ) { item ->
                        ListItem(
                            modifier = Modifier.animateItem(),
                            title = item.name,
                            description = item.muscles,
                            iconPainter = painterResource(id = item.type.iconRes),
                            actions = {
                                IconButton(onClick = { state.removePickedExercise(item.id) }) {
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

            Button(
                colors = ButtonDefaults.filledTonalButtonColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = LocalDimens.current.padding.itemVerticalSmall)
                    .padding(horizontal = LocalDimens.current.padding.itemHorizontal)
                    .animateJump(state.errorEffectState, exercises.value.isInvalid),
                onClick = pickExercises,
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = null)
                Spacer(modifier = Modifier.width(LocalDimens.current.button.iconPadding))
                Text(text = stringResource(id = R.string.action_add_exercise))
            }
        }
    }
}

@Composable
private fun EmptyState(
    state: NewRoutineState,
    modifier: Modifier = Modifier,
) {
    val normalColor = MaterialTheme.colorScheme.onSurfaceVariant
    val errorColor = MaterialTheme.colorScheme.error

    val showErrors by state.showErrors
    val color = animateColorAsState(if (showErrors) errorColor else normalColor, label = "color")

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = MaterialTheme.dimens.verticalItemSpacing)
                .size(128.dp),
            painter = painterResource(id = R.drawable.ic_weightlifter_down),
            contentDescription = null,
            tint = color.value.copy(alpha = IllustrationAlpha),
        )

        Text(
            text = stringResource(id = R.string.state_no_exercises),
            style = MaterialTheme.typography.bodyMedium,
            color = color.value,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = MaterialTheme.dimens.verticalItemSpacing),
        )
    }
}

@MultiDevicePreview
@Composable
private fun NewRoutinePreview() {
    LiftAppTheme {
        val savedStateHandle = remember { SavedStateHandle() }
        NewRoutineScreen(
            state = NewRoutineState(
                routineID = ID_NOT_SET,
                savedStateHandle = savedStateHandle,
                getRoutine = { RoutineWithExercises(0, "", emptyList(), emptyList(), emptyList(), emptyList()) },
                upsertRoutine = { _, _, _ -> },
                getExerciseItems = { flowOf(emptyList()) },
                textFieldStateManager = TextFieldStateManager(
                    stringProvider = PreviewResource.stringProvider,
                    formatter = PreviewResource.formatter(),
                    savedStateHandle = savedStateHandle,
                ),
                validateExercises = NonEmptyCollectionValidator(PreviewResource.stringProvider),
                coroutineScope = rememberCoroutineScope { Dispatchers.Unconfined },
            ),
            navigator = interfaceStub()
        )
    }
}
