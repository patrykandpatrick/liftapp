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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.plan.model.Action
import com.patrykandpatrick.liftapp.ui.component.SinHorizontalDivider
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.graphics.rememberBottomSinShape
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.getHighlightedColorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.date.formatDateRange
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.ui.DayIndicator
import com.patrykandpatryk.liftapp.core.ui.routine.RestCard
import com.patrykandpatryk.liftapp.core.ui.routine.RoutineCard
import com.patrykandpatryk.liftapp.domain.plan.Plan

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
        verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVertical),
        contentPadding = PaddingValues(bottom = dimens.padding.contentVertical),
        modifier = modifier.fillMaxSize(),
    ) {
        stickyHeader { Header(planState) }

        itemsIndexed(planState.plan.items) { index, item ->
            PlanItem(item, index, index == planState.currentPlanItemIndex, onAction)
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
                    .background(colorScheme.background)
                    .padding(
                        horizontal = dimens.padding.contentHorizontal,
                        vertical = dimens.padding.itemVertical,
                    )
                    .padding(bottom = dimens.divider.sinHeight)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text =
                    LocalMarkupProcessor.current.toAnnotatedString(
                        stringResource(
                            R.string.plan_cycle_number_title,
                            planState.cycleNumber,
                            planState.cycleCount,
                        )
                    ),
                style = typography.titleLarge,
            )

            val (startDate, endDate) = planState.cycleDates[planState.cycleNumber]

            Text(text = formatDateRange(startDate, endDate), style = typography.titleSmall)
        }
        SinHorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun PlanItem(
    planItem: Plan.Item,
    dayIndex: Int,
    highlighted: Boolean,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = dimens.padding.contentHorizontal),
        horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontalSmall),
    ) {
        DayIndicator(dayIndex = dayIndex, highlighted = highlighted)

        val colorScheme = if (highlighted) getHighlightedColorScheme() else colorScheme

        MaterialTheme(colorScheme = colorScheme) {
            val colors =
                CardDefaults.outlinedCardColors(
                    containerColor =
                        if (highlighted) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                )
            when (planItem) {
                is Plan.Item.Rest -> {
                    OutlinedCard(modifier = Modifier.fillMaxWidth(), colors = colors) { RestCard() }
                }
                is Plan.Item.Routine -> {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = colors,
                        onClick = { (onAction(Action.OnPlanItemClick(planItem))) },
                    ) {
                        RoutineCard(planItem.routine)
                    }
                }
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun ActivePlanScreenPreview() {
    LiftAppTheme { Surface { ActivePlanScreen(planState = previewActivePlanState, onAction = {}) } }
}
