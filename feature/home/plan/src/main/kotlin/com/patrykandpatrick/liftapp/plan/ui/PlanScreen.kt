package com.patrykandpatrick.liftapp.plan.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.plan.model.Action
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewRoutineWithExercises
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.plan.Plan
import java.time.LocalDate

@Composable
fun PlanScreen(modifier: Modifier = Modifier, padding: PaddingValues = PaddingValues(0.dp)) {
    val viewModel: PlanViewModel = hiltViewModel()
    val state = viewModel.state.collectAsState().value

    PlanScreen(state, viewModel::onAction, modifier.padding(padding))
}

@Composable
private fun PlanScreen(
    state: Loadable<PlanState>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (isEditVisible, setEditVisible) = rememberSaveable { mutableStateOf(false) }

    LiftAppScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CompactTopAppBar(
                title = {
                    CompactTopAppBarDefaults.Title(stringResource(R.string.route_active_plan_full))
                },
                actions = {
                    state.Unfold { state ->
                        if (state is PlanState.ActivePlan) {
                            CompactTopAppBarDefaults.IconButton(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = stringResource(R.string.action_edit),
                            ) {
                                setEditVisible(true)
                            }
                        }
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { paddingValues ->
        state.Unfold(modifier = Modifier.padding(paddingValues).fillMaxSize()) { planState ->
            when (planState) {
                is PlanState.ActivePlan -> ActivePlanScreen(planState, onAction)
                PlanState.NoActivePlan -> NoActivePlanScreen(onAction)
            }
        }
    }

    if (isEditVisible) {
        EditBottomSheet(onDismissRequest = { setEditVisible(false) }, onAction = onAction)
    }
}

internal val previewActivePlanState: PlanState.ActivePlan
    get() =
        PlanState.ActivePlan(
            plan =
                Plan(
                    id = 1,
                    name = "Full Body Workout",
                    description = "",
                    items =
                        listOf(
                            Plan.Item.Routine(PreviewRoutineWithExercises.routines[0]),
                            Plan.Item.Rest,
                            Plan.Item.Routine(PreviewRoutineWithExercises.routines[1]),
                            Plan.Item.Rest,
                            Plan.Item.Routine(PreviewRoutineWithExercises.routines[2]),
                            Plan.Item.Rest,
                        ),
                ),
            cycleNumber = 1,
            cycleCount = 6,
            currentPlanItemIndex = 2,
            cycleDates = PlanState.getAllCycleDates(LocalDate.now().minusDays(7), 6, 6L),
        )

@MultiDevicePreview
@Composable
private fun PlanScreenPreview() {
    PreviewTheme { PlanScreen(state = Loadable.Success(previewActivePlanState), onAction = {}) }
}

@MultiDevicePreview
@Composable
private fun NoActivePlanScreenPreview() {
    PreviewTheme { PlanScreen(Loadable.Success(PlanState.NoActivePlan), {}) }
}
