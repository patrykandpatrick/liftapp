package com.patrykandpatrick.liftapp.plan.list.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.extension.joinToPrettyString
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.ui.BottomAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.plan.list.model.Action
import com.patrykandpatrick.liftapp.plan.list.model.ScreenState
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppFAB
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppRadioButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppRadioButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plan
import com.patrykandpatrick.liftapp.ui.icons.Plus
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun PlanListScreen(modifier: Modifier = Modifier, viewModel: PlanListViewModel = hiltViewModel()) {
    val screenState = viewModel.screenState.collectAsStateWithLifecycle().value

    PlanListScreen(screenState = screenState, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun PlanListScreen(
    screenState: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val dimens = LocalDimens.current
    val fabHeight = 24.dp + dimens.fab.verticalPadding * 2
    val bottomContentPadding = fabHeight + dimens.screen.padding * 2

    screenState.Unfold { state ->
        LiftAppScaffold(
            modifier =
                modifier.fillMaxSize().nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            topBar = { Toolbar(state.isPickingTrainingPlan, onAction, topAppBarScrollBehavior) },
            floatingActionButton = {
                LiftAppFAB(
                    content = {
                        Icon(LiftAppIcons.Plus, contentDescription = null)
                        Text(stringResource(R.string.route_new_plan))
                    },
                    onClick = { onAction(Action.AddNewPlan) },
                )
            },
            bottomBar = {
                if (state.isPickingTrainingPlan) {
                    BottomAppBar.Save(
                        onClick = { onAction(Action.SaveSelection) },
                        enabled = state.isAnyPlanSelected,
                    )
                }
            },
        ) { paddingValues ->
            if (state.plans.isEmpty()) {
                EmptyState(
                    icon = LiftAppIcons.Plan,
                    message = stringResource(R.string.state_no_training_plans),
                    modifier =
                        Modifier.fillMaxSize()
                            .padding(paddingValues)
                            .padding(
                                start = dimens.screen.padding,
                                top = dimens.screen.padding,
                                end = dimens.screen.padding,
                                bottom = dimens.screen.padding,
                            ),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding =
                        PaddingValues(
                            start = dimens.screen.padding,
                            top = dimens.screen.padding,
                            end = dimens.screen.padding,
                            bottom = bottomContentPadding,
                        ),
                ) {
                    itemsIndexed(items = state, key = { _, plan -> plan.id }) { index, plan ->
                        PlanItem(
                            plan = plan,
                            isPickingTrainingPlan = state.isPickingTrainingPlan,
                            position = LiftAppListItemPosition(index, state.plans.size),
                            nextItemSelected = state.plans.getOrNull(index + 1)?.isChecked == true,
                            onAction = onAction,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Toolbar(
    isPickingTrainingPlan: Boolean,
    onAction: (Action) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    CompactTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            val title =
                if (isPickingTrainingPlan) {
                    stringResource(R.string.route_training_plans_select)
                } else {
                    stringResource(R.string.route_training_plans)
                }
            CompactTopAppBarDefaults.Title(title)
        },
        navigationIcon = { CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) } },
        modifier = modifier,
    )
}

@Composable
private fun PlanItem(
    plan: ScreenState.PlanItem,
    isPickingTrainingPlan: Boolean,
    position: LiftAppListItemPosition,
    nextItemSelected: Boolean,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppListItem(
        icon =
            if (isPickingTrainingPlan) {
                {
                    LiftAppRadioButton(
                        selected = plan.isChecked,
                        onCheck = null,
                        colors = LiftAppRadioButtonDefaults.onSurfaceColors,
                    )
                }
            } else {
                null
            },
        title = { Text(plan.name ?: stringResource(R.string.training_plan_name_placeholder)) },
        description = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = plan.routineNames.joinToPrettyString())
                Text(
                    text = stringResource(R.string.training_plan_cycle_length, plan.cycleLength),
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.foreground,
                    modifier =
                        Modifier.background(
                                color = colorScheme.primaryDisabled,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .border(
                                width = 1.dp,
                                color = colorScheme.primary,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }
        },
        position = position,
        contentPadding = PaddingValues(16.dp),
        selected = plan.isChecked.takeIf { isPickingTrainingPlan },
        nextItemSelected = isPickingTrainingPlan && nextItemSelected,
        onClick = { onAction(Action.OnPlanClick(plan.id)) },
        modifier = modifier,
    )
}

@MultiDevicePreview
@Composable
private fun PlanListScreenPreview_Normal() {
    PlanListScreenPreview(isPickingTrainingPlan = false)
}

@MultiDevicePreview
@Composable
private fun PlanListScreenPreview_Picking() {
    PlanListScreenPreview(isPickingTrainingPlan = true)
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
private fun PlanListScreenPreview(isPickingTrainingPlan: Boolean) {
    LiftAppTheme {
        PlanListScreen(
            screenState =
                Loadable.Success(
                    ScreenState(
                        plans =
                            listOf(
                                ScreenState.PlanItem(
                                    id = 1,
                                    name = "Plan 1",
                                    cycleLength = 7,
                                    routineNames = listOf("Routine 1", "Routine 2", "Routine 3"),
                                    isChecked = true,
                                ),
                                ScreenState.PlanItem(
                                    id = 2,
                                    name = "Plan 2",
                                    cycleLength = 7,
                                    routineNames = listOf("Routine 4", "Routine 5", "Routine 6"),
                                    isChecked = false,
                                ),
                                ScreenState.PlanItem(
                                    id = 3,
                                    name = "Plan 3",
                                    cycleLength = 4,
                                    routineNames = listOf("Routine 7", "Routine 8"),
                                    isChecked = false,
                                ),
                            ),
                        isPickingTrainingPlan = isPickingTrainingPlan,
                        isAnyPlanSelected = true,
                    )
                ),
            onAction = {},
        )
    }
}
