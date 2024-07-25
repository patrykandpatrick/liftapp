package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.OutlinedTextField
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.resource.iconRes
import com.patrykandpatryk.liftapp.core.ui.theme.Colors.IllustrationAlpha
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.feature.newroutine.navigation.NewRoutineNavigator

@Composable
fun NewRoutine(
    routineID: Long,
    navigator: NewRoutineNavigator,
    modifier: Modifier = Modifier,
) {
    val viewModel: NewRoutineViewModel = hiltViewModel(
        creationCallback = { factory: NewRoutineViewModel.Factory -> factory.create(routineID) },
    )
    val routineSaved by viewModel.routineSaved
    val routineNotFound by viewModel.routineNotFound

    navigator.RegisterResultListener(key = Constants.Keys.PICKED_EXERCISE_IDS) { pickedExerciseIds: List<Long> ->
        viewModel.addPickedExercises(pickedExerciseIds)
    }

    NewRoutine(
        state = viewModel,
        navigator = navigator,
        modifier = modifier,
    )

    LaunchedEffect(routineNotFound, routineSaved) {
        if (routineNotFound || routineSaved) navigator.back()
    }
}

@Composable
private fun NewRoutine(
    state: NewRoutineState,
    navigator: NewRoutineNavigator,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .imePadding(),
        topBar = {
            val titleRes = if (state.isEdit) R.string.title_edit_routine else R.string.title_new_routine

            TopAppBar(
                title = stringResource(id = titleRes),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = navigator::back,
            )
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = LocalDimens.current.padding.contentVertical),
                    text = stringResource(id = R.string.action_save),
                    icon = painterResource(id = R.drawable.ic_save),
                    onClick = state::save,
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
        ) {
            content(
                state = state,
                navigator = navigator,
            )
        }
    }
}

private fun LazyListScope.content(
    state: NewRoutineState,
    navigator: NewRoutineNavigator,
) {
    stickyHeader {
        Column(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
        ) {
            OutlinedTextField(
                textFieldState = state.name,
                label = { Text(text = stringResource(id = R.string.generic_name)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { state.save() }),
                maxLines = LocalDimens.current.input.nameMaxLines,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LocalDimens.current.padding.contentHorizontal),
            )

            ListSectionTitle(
                modifier = Modifier.padding(top = LocalDimens.current.verticalItemSpacing),
                title = stringResource(id = R.string.title_exercises),
            )
        }
    }

    if (state.exercises.value.isInvalid) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                EmptyState(state)
            }
        }
    } else {
        items(
            items = state.exercises.value.value,
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

    item(key = R.string.action_add_exercise) {
        Button(
            colors = ButtonDefaults.filledTonalButtonColors(),
            modifier = Modifier
                .animateItem()
                .fillMaxWidth()
                .padding(
                    top = LocalDimens.current.verticalItemSpacing,
                    start = LocalDimens.current.padding.contentHorizontal,
                    end = LocalDimens.current.padding.contentHorizontal,
                ),
            onClick = { navigator.pickExercises(state.exerciseIds) },
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = null)

            Spacer(modifier = Modifier.width(LocalDimens.current.button.iconPadding))

            Text(text = stringResource(id = R.string.action_add_exercise))
        }
    }
}

@Composable
private fun ColumnScope.EmptyState(state: NewRoutineState) {
    val normalColor = MaterialTheme.colorScheme.onSurfaceVariant
    val errorColor = MaterialTheme.colorScheme.error

    val color = remember { Animatable(normalColor) }
    val showErrors by state.showErrors

    LaunchedEffect(key1 = showErrors) {
        if (showErrors) {
            color.animateTo(errorColor, tween())
        } else {
            color.animateTo(normalColor, tween())
        }
    }

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
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(bottom = MaterialTheme.dimens.verticalItemSpacing),
        text = stringResource(id = R.string.state_no_exercises),
        style = MaterialTheme.typography.bodyMedium,
        color = color.value,
    )
}

@MultiDevicePreview
@Composable
fun NewRoutinePreview() {
    LiftAppTheme {
        // TODO
    }
}
