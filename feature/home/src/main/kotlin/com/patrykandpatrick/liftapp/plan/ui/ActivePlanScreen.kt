package com.patrykandpatrick.liftapp.plan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.date.formatDateRange
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.ui.DayIndicator
import com.patrykandpatrick.liftapp.core.ui.routine.RestCard
import com.patrykandpatrick.liftapp.core.ui.routine.RoutineCard
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.feature.dashboard.ui.WorkoutStatusWithDate
import com.patrykandpatrick.liftapp.plan.model.Action
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.component.SinHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.appendBulletSeparator
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.graphics.rememberBottomSinShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
internal fun ActivePlanScreen(
    planState: PlanState.ActivePlan,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = dimens.screen.verticalPadding),
        modifier = modifier.fillMaxSize(),
    ) {
        stickyHeader { Header(planState) }

        itemsIndexed(planState.plan.items) { index, item ->
            PlanItem(
                planItem = item,
                dayIndex = index,
                isActive = index == planState.currentPlanItemIndex,
                workout =
                    planState.currentWorkout.takeIf { index == planState.currentPlanItemIndex },
                onAction = onAction,
            )
        }
    }

    LaunchedEffect(Unit) { lazyListState.scrollToItem(planState.currentPlanItemIndex) }
}

@Composable
private fun Header(planState: PlanState.ActivePlan, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Column(
            modifier =
                Modifier.clip(rememberBottomSinShape())
                    .background(colorScheme.surface)
                    .padding(
                        horizontal = dimens.screen.horizontalPadding,
                        vertical = 16.dp,
                    )
                    .padding(bottom = dimens.divider.sinHeight)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.plan_cycle_title),
                style = typography.titleLarge,
            )

            val (startDate, endDate) = planState.cycleDates[planState.cycleNumber]

            LiftAppText(
                text =
                    buildAnnotatedString {
                        append("${planState.cycleNumber + 1}/${planState.cycleCount}")
                        appendBulletSeparator()
                        append(formatDateRange(startDate, endDate))
                    },
                style = typography.titleSmall,
            )
        }
        SinHorizontalDivider(
            color = colorScheme.divider,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun PlanItem(
    planItem: Plan.Item,
    dayIndex: Int,
    isActive: Boolean,
    workout: Workout?,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = dimens.screen.horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DayIndicator(dayIndex = dayIndex, highlighted = isActive)

        val colors =
            if (isActive) {
                LiftAppCardDefaults.tonalCardColors
            } else {
                LiftAppCardDefaults.cardColors
            }

        when (planItem) {
            is Plan.Item.Rest -> {
                LiftAppCard(modifier = Modifier.fillMaxWidth(), colors = colors) { RestCard() }
            }

            is Plan.Item.Routine -> {
                LiftAppCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = colors,
                    onClick = { (onAction(Action.OnPlanItemClick(planItem))) },
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        workout?.let { WorkoutStatusWithDate(it) }
                        RoutineCard(
                            routineWithExercises = planItem.routine,
                            actionsRow = {
                                if (isActive) {
                                    PlainLiftAppButton(
                                        onClick = {
                                            if (workout == null) {
                                                onAction(Action.StartWorkout(planItem))
                                            } else {
                                                onAction(Action.GoToWorkout(workout.id))
                                            }
                                        }
                                    ) {
                                        Text(
                                            stringResource(
                                                when {
                                                    workout == null -> R.string.action_start_workout
                                                    workout.isCompleted -> R.string.action_show
                                                    else -> R.string.action_continue
                                                }
                                            )
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun ActivePlanScreenPreview() {
    PreviewTheme { ActivePlanScreen(planState = previewActivePlanState, onAction = {}) }
}
