package com.patrykandpatrick.liftapp.plan.creator.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.preview.PreviewRoutineWithExercises
import com.patrykandpatrick.liftapp.core.ui.routine.RestCard
import com.patrykandpatrick.liftapp.core.ui.routine.RoutineCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plus
import com.patrykandpatrick.liftapp.ui.icons.Routine
import com.patrykandpatrick.liftapp.ui.icons.TreePalm
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

@Composable
internal fun PlanCreatorItem(
    item: ScreenState.Item,
    onAddRestDayClick: () -> Unit,
    onAddRoutineClick: () -> Unit,
    onClick: (ScreenState.Item.RoutineItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppCard(
        // Only routines lead anywhere. A rest day has nothing to show, so it stays inert rather
        // than being clickable and doing nothing.
        onClick =
            if (item is ScreenState.Item.RoutineItem) {
                { onClick(item) }
            } else {
                null
            },
        modifier = modifier,
        contentPadding =
            if (item is ScreenState.Item.PlaceholderItem) {
                PaddingValues(vertical = 16.dp)
            } else {
                PaddingValues(16.dp)
            },
    ) {
        AnimatedContent(item) { planItem ->
            when (planItem) {
                ScreenState.Item.PlaceholderItem ->
                    PlaceholderItem(onAddRestDayClick, onAddRoutineClick)

                is ScreenState.Item.RestItem -> RestCard()
                is ScreenState.Item.RoutineItem -> RoutineCard(planItem.routine)
            }
        }
    }
}

@Composable
private fun PlaceholderItem(
    onRestDayClick: () -> Unit,
    onRoutineClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.height(IntrinsicSize.Min)) {
        PlaceholderItemButton(
            text = stringResource(R.string.training_plan_item_add_rest_day),
            icon = LiftAppIcons.TreePalm,
            onClick = onRestDayClick,
            modifier = Modifier.weight(1f),
        )
        VerticalDivider(modifier = Modifier)
        PlaceholderItemButton(
            text = stringResource(R.string.training_plan_item_add_routine),
            icon = LiftAppIcons.Routine,
            onClick = onRoutineClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PlaceholderItemButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier.clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Icon(
                imageVector = LiftAppIcons.Plus,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Icon(imageVector = icon, contentDescription = null)
        }
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun PlanCreatorItemItemPreview(item: ScreenState.Item) {
    LiftAppTheme {
        LiftAppBackground {
            PlanCreatorItem(
                item = item,
                onAddRestDayClick = {},
                onAddRoutineClick = {},
                onClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
@LightAndDarkThemePreview
private fun PlanCreatorItemPlaceholderItemPreview() {
    PlanCreatorItemItemPreview(ScreenState.Item.PlaceholderItem)
}

@Composable
@LightAndDarkThemePreview
private fun PlanCreatorItemRestItemPreview() {
    PlanCreatorItemItemPreview(ScreenState.Item.RestItem())
}

@Composable
@LightAndDarkThemePreview
private fun PlanCreatorItemRoutineItemPreview() {
    PlanCreatorItemItemPreview(
        ScreenState.Item.RoutineItem(PreviewRoutineWithExercises.routines[0])
    )
}
