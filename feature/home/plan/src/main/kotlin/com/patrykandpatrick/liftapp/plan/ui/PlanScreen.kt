package com.patrykandpatrick.liftapp.plan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.liftapp.plan.model.Action
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.interfaceStub
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.plan.Plan

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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.route_active_plan_full)) }
            )
        },
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues).fillMaxSize(), Alignment.Center) {
            state.Unfold { planState ->
                when (planState) {
                    is PlanState.ActivePlan -> ActivePlanScreen(planState.plan)
                    PlanState.NoActivePlan -> NoActivePlanScreen(onAction)
                }
            }
        }
    }
}

@Composable
private fun BoxScope.ActivePlanScreen(plan: Plan) {
    Text(
        text = plan.name,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun BoxScope.NoActivePlanScreen(onAction: (Action) -> Unit) {
    val padding = LocalDimens.current.padding
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier.align(Alignment.Center)
                .padding(
                    horizontal = padding.contentHorizontal,
                    vertical = padding.contentVertical,
                ),
        verticalArrangement = Arrangement.spacedBy(padding.itemVerticalSmall),
    ) {
        Text(
            text = stringResource(R.string.plan_no_active_plan),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = padding.itemVertical),
        )

        Button(onClick = { onAction(Action.ChooseExistingPlan) }) {
            Icon(
                painter = painterResource(R.drawable.ic_open),
                contentDescription = null,
                modifier = Modifier.padding(end = padding.itemHorizontalSmall),
            )

            Text(
                stringResource(R.string.plan_no_active_plan_choose_existing_cta)
            ) // TODO show conditionally
        }

        OutlinedButton(onClick = { onAction(Action.CreateNewPlan) }) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.padding(end = padding.itemHorizontalSmall),
            )
            Text(stringResource(R.string.plan_no_active_plan_create_cta))
        }
    }
}

@MultiDevicePreview
@Composable
private fun PlanScreenPreview() {
    LiftAppTheme {
        PlanScreen(
            Loadable.Success(
                PlanState.ActivePlan(
                    Plan(id = 1, name = "Full Body Workout", description = "", items = emptyList())
                )
            ),
            interfaceStub(),
        )
    }
}

@MultiDevicePreview
@Composable
private fun NoActivePlanScreenPreview() {
    LiftAppTheme { PlanScreen(Loadable.Success(PlanState.NoActivePlan), interfaceStub()) }
}
