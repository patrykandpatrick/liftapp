package com.patrykandpatrick.liftapp.plan.creator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.liftapp.plan.creator.model.Action
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.core.ui.OutlinedTextField
import com.patrykandpatryk.liftapp.core.ui.SinHorizontalDivider
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.domain.model.Loadable

@Composable
fun PlanCreatorScreen(modifier: Modifier = Modifier) {
    val viewModel: PlanCreatorViewModel = hiltViewModel()

    val state = viewModel.state.collectAsState().value

    PlanCreatorScreen(state, viewModel::onAction, modifier)
}

@Composable
private fun PlanCreatorScreen(
    loadableState: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    loadableState.Unfold { state ->
        val errorText = state.error?.getText()

        LaunchedEffect(errorText) {
            if (errorText == null) return@LaunchedEffect
            snackbarHostState.showSnackbar(errorText)
            onAction(Action.ClearError)
        }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                CompactTopAppBar(
                    title = { Title(state.isEdit) },
                    navigationIcon = {
                        CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) }
                    },
                )
            },
            bottomBar = { BottomAppBar.Save(onClick = { onAction(Action.Save(state)) }) },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier =
                        Modifier.padding(
                            horizontal = LocalDimens.current.padding.contentHorizontal,
                            vertical = LocalDimens.current.padding.itemVertical,
                        ),
                ) { snackbarData ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_error),
                                contentDescription = null,
                                modifier =
                                    Modifier.padding(
                                        end = LocalDimens.current.padding.itemHorizontalSmall
                                    ),
                            )
                            Text(text = snackbarData.visuals.message)
                        }
                    }
                }
            },
        ) { paddingValues ->
            LazyColumn(
                contentPadding =
                    PaddingValues(vertical = LocalDimens.current.padding.contentVertical),
                verticalArrangement =
                    Arrangement.spacedBy(LocalDimens.current.padding.itemVertical),
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            ) {
                items(state, onAction)
            }
        }
    }
}

@Composable
private fun Title(isEdit: Boolean, modifier: Modifier = Modifier) {
    Text(
        text =
            if (isEdit) {
                stringResource(R.string.route_new_plan_edit)
            } else {
                stringResource(R.string.route_new_plan)
            },
        modifier = modifier,
    )
}

private fun LazyListScope.items(state: ScreenState, onAction: (Action) -> Unit) {
    item(key = "input") {
        EditableDetails(
            state,
            Modifier.padding(horizontal = LocalDimens.current.padding.contentHorizontal)
                .fillMaxWidth()
                .animateItem(),
        )
    }

    item(key = "details_divider", contentType = "divider") {
        SinHorizontalDivider(Modifier.animateItem())
    }

    itemsIndexed(items = state.items, key = { _, item -> item.id }) { index, item ->
        PlanCreatorItem(
            index = index,
            item = item,
            onAction = onAction,
            modifier = Modifier.animateItem(),
        )
    }
}

@Composable
private fun EditableDetails(state: ScreenState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemVertical),
    ) {
        val focusManager = LocalFocusManager.current

        OutlinedTextField(
            textFieldState = state.name,
            label = { Text(stringResource(R.string.generic_name)) },
            placeholder = { Text(stringResource(R.string.training_plan_name_placeholder)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        OutlinedTextField(
            textFieldState = state.description,
            label = { Text(stringResource(R.string.generic_description)) },
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )
    }
}

@Composable
private fun PlanCreatorItem(
    index: Int,
    item: ScreenState.Item,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    start = LocalDimens.current.padding.contentHorizontal,
                    end = LocalDimens.current.padding.contentHorizontalSmall,
                ),
        horizontalArrangement =
            Arrangement.spacedBy(LocalDimens.current.padding.itemHorizontalSmall),
    ) {
        DayIndicator(dayIndex = index, enabled = item !is ScreenState.Item.PlaceholderItem)

        PlanCreatorItem(
            item = item,
            onAddRestDayClick = { onAction(Action.AddRestDay) },
            onAddRoutineClick = { onAction(Action.AddRoutine) },
            onClick = { onAction(Action.OnPlanElementClick(it)) },
            modifier =
                Modifier.weight(1f)
                    .padding(start = LocalDimens.current.padding.contentHorizontalSmall),
        )

        if (item is ScreenState.Item.PlaceholderItem) {
            Spacer(Modifier.width(LocalDimens.current.iconButton.minTouchTarget))
        } else {
            IconButton(
                onClick = { onAction(Action.RemoveItem(index)) },
                modifier =
                    Modifier.align(
                        if (item is ScreenState.Item.RestItem) Alignment.CenterVertically
                        else Alignment.Top
                    ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_remove_circle),
                    contentDescription = stringResource(R.string.training_plan_item_remove),
                )
            }
        }
    }
}
