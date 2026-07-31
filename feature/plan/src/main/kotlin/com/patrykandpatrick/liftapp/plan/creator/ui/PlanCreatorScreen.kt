package com.patrykandpatrick.liftapp.plan.creator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewResource
import com.patrykandpatrick.liftapp.core.preview.PreviewRoutineWithExercises
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.ui.BottomAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatrick.liftapp.core.ui.DayIndicator
import com.patrykandpatrick.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.plan.creator.model.Action
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialog
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialogDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.component.SinHorizontalDivider
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.CircleMinus
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons

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
    loadableState.Unfold { state ->
        val isDeleteDialogVisible = rememberSaveable { mutableStateOf(false) }

        DeletePlanDialog(
            isVisible = isDeleteDialogVisible.value,
            planName = state.name.value,
            onDismissRequest = { isDeleteDialogVisible.value = false },
            onConfirm = { onAction(Action.DeletePlan(state.id)) },
        )

        val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        LiftAppScaffold(
            modifier =
                modifier.fillMaxSize().nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            topBar = {
                CompactTopAppBar(
                    scrollBehavior = topAppBarScrollBehavior,
                    title = { Title(state.isEdit) },
                    navigationIcon = {
                        CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) }
                    },
                    actions = {
                        // There is nothing to delete until the plan has been saved once.
                        if (state.isEdit) {
                            LiftAppIconButton(onClick = { isDeleteDialogVisible.value = true }) {
                                Icon(
                                    imageVector = LiftAppIcons.Delete,
                                    contentDescription = stringResource(R.string.action_delete),
                                )
                            }
                        }
                    },
                )
            },
            bottomBar = {
                BottomAppBar.Save(
                    onClick = { onAction(Action.Save(state)) },
                    enabled = state.canSave,
                )
            },
        ) { paddingValues ->
            val screenVerticalPadding = LocalDimens.current.screen.verticalPadding
            val floatingLabelTopInset =
                with(LocalDensity.current) {
                    MaterialTheme.typography.bodySmall.lineHeight.toDp() / 2
                }
            LazyColumn(
                contentPadding =
                    PaddingValues(
                        top = screenVerticalPadding - floatingLabelTopInset,
                        bottom = screenVerticalPadding,
                    ),
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            ) {
                items(state, onAction)
            }
        }
    }
}

@Composable
private fun DeletePlanDialog(
    isVisible: Boolean,
    planName: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (isVisible) {
        LiftAppAlertDialog(
            onDismissRequest = onDismissRequest,
            icon = { Icon(LiftAppIcons.Delete, null) },
            title = { Text(text = stringResource(R.string.generic_delete_something, planName)) },
            text = { Text(text = stringResource(R.string.training_plan_delete_message)) },
            dismissButton = {
                LiftAppAlertDialogDefaults.DismissButton(
                    onDismissRequest,
                    stringResource(android.R.string.cancel),
                )
            },
            confirmButton = {
                PlainLiftAppButton(onClick = onConfirm) {
                    Text(text = stringResource(R.string.action_delete))
                }
            },
        )
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
            Modifier.padding(horizontal = LocalDimens.current.screen.horizontalPadding)
                .fillMaxWidth()
                .animateItem(),
        )
    }

    item(key = "details_divider", contentType = "divider") {
        val supportingTextSlotHeight = LocalDimens.current.supportingText.verticalPadding * 2
        SinHorizontalDivider(
            Modifier.padding(top = VISIBLE_SECTION_SPACING - supportingTextSlotHeight).animateItem()
        )
    }

    itemsIndexed(items = state.items, key = { _, item -> item.id }) { index, item ->
        PlanCreatorItem(
            index = index,
            item = item,
            onAction = onAction,
            modifier =
                Modifier.padding(
                        top = if (index == 0) VISIBLE_SECTION_SPACING else ITEM_VERTICAL_SPACING
                    )
                    .animateItem(),
        )
    }
}

@Composable
private fun EditableDetails(state: ScreenState, modifier: Modifier = Modifier) {
    val supportingTextVerticalPadding = LocalDimens.current.supportingText.verticalPadding
    val floatingLabelTopPadding =
        with(LocalDensity.current) { MaterialTheme.typography.bodySmall.lineHeight.toDp() / 2 }
    val outlineSpacing =
        VISIBLE_INPUT_SPACING - supportingTextVerticalPadding * 2 - floatingLabelTopPadding

    Column(
        modifier = modifier,
        // Measure between borders, excluding the empty supporting slot and the next label's inset.
        verticalArrangement = Arrangement.spacedBy(outlineSpacing),
    ) {
        val focusManager = LocalFocusManager.current

        LiftAppTextFieldWithSupportingText(
            textFieldState = state.name,
            label = { Text(stringResource(R.string.generic_name)) },
            placeholder = { Text(stringResource(R.string.training_plan_name_placeholder)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        LiftAppTextFieldWithSupportingText(
            textFieldState = state.description,
            label = { Text(stringResource(R.string.generic_description)) },
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )
    }
}

private val VISIBLE_INPUT_SPACING = 16.dp
private val VISIBLE_SECTION_SPACING = 20.dp
private val ITEM_VERTICAL_SPACING = 16.dp

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
                    start = LocalDimens.current.screen.horizontalPadding,
                    end = LocalDimens.current.screen.horizontalPadding - 8.dp,
                ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DayIndicator(dayIndex = index, highlighted = item !is ScreenState.Item.PlaceholderItem)

        PlanCreatorItem(
            item = item,
            onAddRestDayClick = { onAction(Action.AddRestDay) },
            onAddRoutineClick = { onAction(Action.AddRoutine) },
            onClick = { onAction(Action.OnRoutineClick(it.routine.id)) },
            modifier =
                Modifier.weight(1f)
                    .padding(start = LocalDimens.current.screen.horizontalPadding - 8.dp),
        )

        if (item is ScreenState.Item.PlaceholderItem) {
            Spacer(Modifier.width(LocalDimens.current.iconButton.minTouchTarget))
        } else {
            LiftAppIconButton(
                onClick = { onAction(Action.RemoveItem(index)) },
                modifier =
                    Modifier.align(
                        if (item is ScreenState.Item.RestItem) Alignment.CenterVertically
                        else Alignment.Top
                    ),
            ) {
                Icon(
                    imageVector = LiftAppIcons.CircleMinus,
                    contentDescription = stringResource(R.string.training_plan_item_remove),
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun PlanCreatorScreenPreview() {
    PreviewTheme {
        val textFieldStateManager = PreviewResource.textFieldStateManager()

        PlanCreatorScreen(
            loadableState =
                Loadable.Success(
                    ScreenState(
                        id = 1L,
                        name = textFieldStateManager.stringTextField(),
                        description = textFieldStateManager.stringTextField(),
                        items =
                            listOf(
                                ScreenState.Item.RoutineItem(
                                    PreviewRoutineWithExercises.routines[0]
                                ),
                                ScreenState.Item.RestItem(),
                                ScreenState.Item.RoutineItem(
                                    PreviewRoutineWithExercises.routines[1]
                                ),
                                ScreenState.Item.PlaceholderItem,
                            ),
                    )
                ),
            onAction = {},
        )
    }
}
