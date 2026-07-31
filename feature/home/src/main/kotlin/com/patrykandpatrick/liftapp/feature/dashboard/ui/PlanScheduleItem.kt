package com.patrykandpatrick.liftapp.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.ui.routine.RestCard
import com.patrykandpatrick.liftapp.core.ui.routine.RoutineCard
import com.patrykandpatrick.liftapp.feature.dashboard.model.Action
import com.patrykandpatrick.liftapp.feature.dashboard.model.PlanScheduleItem
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.TreePalm

@Composable
internal fun RestPlanItem(modifier: Modifier = Modifier) {
    LiftAppCard(onClick = null, modifier = modifier.fillMaxWidth()) { RestCard() }
}

@Composable
internal fun NonePlanItem(
    hasActivePlan: Boolean,
    isToday: Boolean,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonPadding = LiftAppButtonDefaults.plainContentPadding
    val layoutDirection = LocalLayoutDirection.current

    LiftAppCard(onClick = null, modifier = modifier.fillMaxWidth()) {
        Icon(imageVector = LiftAppIcons.TreePalm, contentDescription = null)
        LiftAppText(
            text =
                stringResource(
                    if (isToday) {
                        R.string.plan_no_schedule_for_today
                    } else {
                        R.string.plan_no_schedule_for_this_day
                    }
                ),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        PlainLiftAppButton(
            onClick = { onAction(Action.Navigate(Routes.Home.Plan)) },
            modifier =
                Modifier.align(Alignment.End)
                    .padding(top = 16.dp)
                    .offset(
                        x = buttonPadding.calculateEndPadding(layoutDirection),
                        y = buttonPadding.calculateBottomPadding(),
                    ),
        ) {
            LiftAppText(
                text =
                    stringResource(
                        if (hasActivePlan) {
                            R.string.plan_see_training_plan
                        } else {
                            R.string.training_plan_add_new
                        }
                    )
            )
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
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
}
