package com.patrykandpatryk.liftapp.feature.dashboard.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.TreePalm
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.routine.RestCard
import com.patrykandpatryk.liftapp.core.ui.routine.RoutineCard
import com.patrykandpatryk.liftapp.feature.dashboard.model.Action
import com.patrykandpatryk.liftapp.feature.dashboard.model.PlanScheduleItem

@Composable
internal fun RestPlanItem(modifier: Modifier = Modifier) {
    LiftAppCard(onClick = null, modifier = modifier.fillMaxWidth()) { RestCard() }
}

@Composable
internal fun NonePlanItem(onAction: (Action) -> Unit, modifier: Modifier = Modifier) {
    LiftAppCard(onClick = null, modifier = modifier.fillMaxWidth()) {
        Icon(imageVector = LiftAppIcons.TreePalm, contentDescription = null)
        LiftAppText(
            text = stringResource(R.string.plan_no_schedule_for_this_day),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = dimens.padding.itemVerticalSmall),
        )
        PlainLiftAppButton(
            onClick = { onAction(Action.Navigate(Routes.Home.Plan)) },
            modifier = Modifier.align(Alignment.End).padding(top = dimens.padding.itemVertical),
        ) {
            LiftAppText(text = stringResource(R.string.training_plan_add_new))
        }
    }
}

@Composable
internal fun RoutinePlanItem(
    planItem: PlanScheduleItem.Routine,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppCard(
        onClick = { onAction(Action.GoToRoutine(planItem.routine.id)) },
        modifier = modifier.fillMaxWidth(),
        colors =
            if (planItem.workout?.isCompleted == false) {
                LiftAppCardDefaults.tonalCardColors
            } else {
                LiftAppCardDefaults.cardColors
            },
    ) {
        planItem.workout?.also { workout -> WorkoutStatusWithDate(workout) }

        RoutineCard(
            routineWithExercises = planItem.routine,
            actionsRow = {
                when {
                    planItem.workout != null ->
                        PlainLiftAppButton(
                            onClick = { onAction(Action.GoToWorkout(planItem.workout.id)) }
                        ) {
                            Text(
                                text =
                                    if (planItem.workout.isCompleted) {
                                        stringResource(R.string.action_show)
                                    } else {
                                        stringResource(R.string.action_continue)
                                    }
                            )
                        }

                    else ->
                        PlainLiftAppButton(
                            onClick = { onAction(Action.NewWorkout(planItem.routine.id)) }
                        ) {
                            Text(stringResource(R.string.action_start_workout))
                        }
                }
            },
        )
    }
}
