package com.patrykandpatrick.liftapp.plan.list.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.plan.list.model.Action
import com.patrykandpatrick.liftapp.plan.list.model.ScreenState
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.joinToPrettyString
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.domain.model.Loadable

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
    screenState.Unfold { state ->
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = { Toolbar(state.isPickingTrainingPlan, onAction) },
            bottomBar = {
                if (state.isPickingTrainingPlan) {
                    BottomAppBar.Save(
                        onClick = { onAction(Action.SaveSelection) },
                        enabled = state.isAnyPlanSelected,
                    )
                }
            },
        ) { paddingValues ->
            val dimens = LocalDimens.current
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(items = state, key = { it.id }) { plan ->
                    PlanItem(
                        plan = plan,
                        isPickingTrainingPlan = state.isPickingTrainingPlan,
                        onAction = onAction,
                    )

                    HorizontalDivider(
                        Modifier.padding(
                            start =
                                dimens.padding.contentHorizontal +
                                    if (state.isPickingTrainingPlan) 64.dp else 0.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun Toolbar(
    isPickingTrainingPlan: Boolean,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    CompactTopAppBar(
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
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        icon =
            if (isPickingTrainingPlan) {
                {
                    RadioButton(
                        selected = plan.isChecked,
                        onClick = { onAction(Action.CheckPlan(plan.id)) },
                    )
                }
            } else null,
        title = { Text(plan.name) },
        description = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = plan.routineNames.joinToPrettyString())
                Text(
                    text = stringResource(R.string.training_plan_cycle_length, plan.cycleLength),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier =
                        Modifier.background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }
        },
        modifier = modifier.clickable(onClick = { onAction(Action.CheckPlan(plan.id)) }),
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
