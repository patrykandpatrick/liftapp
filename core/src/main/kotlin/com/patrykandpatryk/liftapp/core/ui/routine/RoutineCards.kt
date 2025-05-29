package com.patrykandpatryk.liftapp.core.ui.routine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises

@Composable
fun RestCard(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues =
        PaddingValues(
            horizontal = dimens.padding.itemHorizontal,
            vertical = dimens.padding.itemVertical,
        ),
) {
    Row(
        horizontalArrangement =
            Arrangement.spacedBy(dimens.padding.itemHorizontal, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(paddingValues),
    ) {
        Icon(painter = painterResource(R.drawable.ic_rest_day), contentDescription = null)
        Text(
            text = stringResource(R.string.training_plan_item_rest_day),
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
fun RoutineCard(
    routineWithExercises: RoutineWithExercises,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues =
        PaddingValues(
            horizontal = dimens.padding.itemHorizontal,
            vertical = dimens.padding.itemVertical,
        ),
    actionsRow: @Composable RowScope.() -> Unit = {},
) {
    RoutineCard(
        routineName = routineWithExercises.name,
        exerciseNames = routineWithExercises.exercises.map { it.name },
        modifier = modifier,
        paddingValues = paddingValues,
        actionsRow = actionsRow,
    )
}

@Composable
fun RoutineCard(
    routineName: String,
    exerciseNames: List<String>,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues =
        PaddingValues(
            horizontal = dimens.padding.itemHorizontal,
            vertical = dimens.padding.itemVertical,
        ),
    actionsRow: @Composable RowScope.() -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(dimens.padding.contentVerticalSmall),
    ) {
        Text(text = routineName, style = MaterialTheme.typography.titleMedium)

        Text(
            text =
                buildString {
                    exerciseNames.forEach { name ->
                        if (isNotEmpty()) append("\n")
                        append("• $name")
                    }
                },
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontalSmall),
            content = actionsRow,
        )
    }
}
