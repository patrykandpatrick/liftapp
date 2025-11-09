package com.patrykandpatrick.liftapp.plan.creator.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.PreviewRoutineWithExercises
import com.patrykandpatryk.liftapp.core.ui.routine.RestCard
import com.patrykandpatryk.liftapp.core.ui.routine.RoutineCard

@Composable
internal fun PlanCreatorItem(
    item: ScreenState.Item,
    onAddRestDayClick: () -> Unit,
    onAddRoutineClick: () -> Unit,
    onClick: (ScreenState.Item.PlanElement) -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppCard(
        onClick =
            if (item is ScreenState.Item.PlanElement) {
                { onClick(item) }
            } else null,
        modifier = modifier,
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
            icon = painterResource(R.drawable.ic_rest_day),
            onClick = onRestDayClick,
            modifier = Modifier.weight(1f),
        )
        VerticalDivider(modifier = Modifier)
        PlaceholderItemButton(
            text = stringResource(R.string.training_plan_item_add_routine),
            icon = painterResource(R.drawable.ic_routines_outlined),
            onClick = onRoutineClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PlaceholderItemButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = LocalDimens.current.padding
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .clickable(onClick = onClick)
                .padding(horizontal = padding.itemHorizontal, vertical = padding.itemVertical),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(end = 10.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Icon(painter = icon, contentDescription = null)
        }
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun PlanCreatorItemItemPreview(item: ScreenState.Item) {
    LiftAppTheme {
        Surface {
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
